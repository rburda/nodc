package com.burda.scraper.inventory;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.joda.time.LocalDate;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.burda.scraper.dao.HotelDetailCacheKey;
import com.burda.scraper.dao.HotelDetailDAO;
import com.burda.scraper.dao.SourceHotelDAO;
import com.burda.scraper.model.Amenity;
import com.burda.scraper.model.DailyRate;
import com.burda.scraper.model.Hotel;
import com.burda.scraper.model.Photo;
import com.burda.scraper.model.RoomType;
import com.burda.scraper.model.SearchParams;
import com.burda.scraper.model.persisted.HotelDetail;
import com.burda.scraper.model.persisted.InventorySource;
import com.burda.scraper.model.persisted.RoomTypeDetail;
import com.burda.scraper.model.persisted.SourceHotel;
import com.google.code.ssm.api.format.SerializationType;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class NODCWarehouse implements Warehouse
{
	private static final DateFormat STAY_DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy");
	static final Logger logger = LoggerFactory.getLogger(NODCWarehouse.class);
	
	private static final ExecutorService asyncResultsThreadPool = Executors.newCachedThreadPool();

	private static final class CustomRedirectStrategy extends DefaultRedirectStrategy 
	{  
		private String html = null;
		
		public String getRetrievedHtml()
		{
			return html;
		}
		
    public boolean isRedirected(HttpRequest request, HttpResponse response, HttpContext context)  
    {
    	html = null;
    	boolean isRedirect=false;
      try 
      {
      	isRedirect = super.isRedirected(request, response, context);
      } 
      catch (ProtocolException e) 
      {
      	logger.error("exception in redirected", e);
      }
      if (!isRedirect) 
      {
      	int responseCode = response.getStatusLine().getStatusCode();
        if (responseCode == 301 || responseCode == 302) 
        {
        	return true;
        }
        try
        {
        	html = EntityUtils.toString(response.getEntity());
        	isRedirect = (html != null && html.contains("redirectURL"));
        }
        catch (IOException ioe)
        {
        	logger.error("Unable to parse response", ioe);
        }
      }
      return isRedirect;
    }
    
    public URI getLocationURI(final HttpRequest request, 
    		final HttpResponse response, final HttpContext context) throws ProtocolException 
    {
    	if (html != null && html.contains("redirectURL"))
    	{
    		int idx = html.indexOf("http:");
    		int endIdx = html.indexOf("\"})");
    		if (idx >= 0 && endIdx >= 0)
    			response.addHeader("Location", html.substring(idx, endIdx).replace("\\",  ""));
    	}
    	return super.getLocationURI(request, response, context);
    }
}
	
	
	@Autowired
	@Qualifier("sourceHotelDAO")
	private SourceHotelDAO sourceHotelDAO;
	
	@Autowired
	@Qualifier("hotelDetailDAO")
	private HotelDetailDAO hotelDetailDAO;
	
  //@Autowired
  //@Qualifier("defaultMemcachedClient") 
  private com.google.code.ssm.Cache cache;
	
	/**
	 * pulls all possible hotels from NODC inv source. Hotel objects returned only
	 * have the 'source' filed out. Intention is to use this to fill cache of 
	 * known hotels
	 * @return
	 * @throws Exception
	 */
	public List<Hotel> getAllShellHotels() throws Exception
	{
		List<Hotel> hotels = Lists.newArrayList();
		URIBuilder builder = new URIBuilder();
		builder
				.setScheme("http")
				.setHost("www.neworleans.com")
				.setPath("/new-orleans-hotels");
		
		try
		{
			HttpGet httpget = new HttpGet(builder.build());
			
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpParams httpParams = httpClient.getParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, 45000);
	    HttpConnectionParams.setSoTimeout(httpParams, 45000);	
	    
	    HttpResponse resp = httpClient.execute(httpget);
	    Document d = Jsoup.parse(EntityUtils.toString(resp.getEntity()));
	    for (Element hotelLink: d.select(".product-price"))
	    {
	    	builder = new URIBuilder();
	    	builder
	    		.setScheme("http")
	    		.setHost("www.neworleans.com")
	    		.setPath(hotelLink.select(".product-title a").first().attr("href"));
	    	resp = httpClient.execute(new HttpGet(builder.build()));
	    	Document indHotelDoc = Jsoup.parse(EntityUtils.toString(resp.getEntity()));
	
				Hotel h = new Hotel();
				h.setName(indHotelDoc.select(".hotel-title").first().ownText());
				SourceHotel source = new SourceHotel();
				
				String extHotelId = indHotelDoc.select("[insertto=hotelWidgetPlaceholder]").first().attr("widget");
				int idx = extHotelId.indexOf("productId=");
				source.setExternalHotelId(extHotelId.substring(idx+10, extHotelId.length()));
				source.setHotelName(h.getName());
				source.setInvSource(com.burda.scraper.model.persisted.InventorySource.NODC);
				h.setSource(source);
				
				HotelDetail detail = new HotelDetail();
				h.setHotelDetails(detail);
				detail.setName(h.getName());
				detail.setDescription(indHotelDoc.select("#tabs #description").first().ownText());
				detail.setAreaDescription(hotelLink.select(".product-area").first().ownText());
				detail.setAddress1(indHotelDoc.select(".adr .address1").first().ownText());
				detail.setCity(indHotelDoc.select(".adr .cityStatePC .locality").first().ownText());
				detail.setState(indHotelDoc.select(".adr .cityStatePC .region").first().ownText());
				detail.setZip(indHotelDoc.select(".adr .cityStatePC .postal-code").first().ownText());
				detail.setLatitude(indHotelDoc.select("[property=v:latitude]").first().attr("content"));
				detail.setLongitude(indHotelDoc.select("[property=v:longitude]").first().attr("content"));
				detail.setRating(Float.valueOf(indHotelDoc.select(".productStars img").first().attr("alt")));
			
				detail.clearAmenities();
				for (Element amenityEl: indHotelDoc.select(".feature-container"))
				{
					Amenity a = new Amenity();
					a.name = amenityEl.select(".feature-name").first().ownText();
					Element descEl = amenityEl.select("p").first();
					if (descEl != null)
						a.description = descEl.ownText();
					else
						a.description = amenityEl.ownText();
					detail.addAmenity(a);
				}
				
				detail.clearPhotos();
				for (Element photoEl: indHotelDoc.select("#thumbs .thumbs .thumb"))
				{
					Photo p = new Photo();
					p.url = "http://www.neworleans.com"+photoEl.select("img").first().attr("src");
					detail.addPhoto(p);
				}
					
				for (Element roomEl: indHotelDoc.select(".tabRoom"))
				{
					RoomTypeDetail rtd = new RoomTypeDetail();
					rtd.setName(roomEl.select(".tabText h4").first().ownText());
					rtd.setHotelName(h.getName());
					StringBuffer desc = new StringBuffer();
					for (Element descEl: roomEl.select(".tabText p"))
					{
						desc.append("<p>");
						desc.append(descEl.html());
						desc.append("<\\p>");
					}
					rtd.setDetails(desc.toString());
					rtd.setFeatures(roomEl.select(".tabText").first().ownText());
					
					Photo p = new Photo();
					p.url = "http://www.neworleans.com"+roomEl.select(".tabText img").first().attr("src");
					rtd.addPhoto(p);
					detail.addRoomTypeDetail(rtd);
				}
				
				
				hotels.add(h);	    	
	    }
	    
		}
		catch (Exception e)
		{
			logger.error("Unable to query shell hotels", e);
		}
		
		return hotels;
	}
	
	@Override
	public Collection<Hotel> 
		getInitialResultsAndAsyncContinue(HttpServletRequest request, SearchParams params) throws Exception
	{
		CountDownLatch initialResultsComplete = new CountDownLatch(1);
		String response = queryNODCHotelsViaHttpClient(params);

		Collection<Hotel> hotels = Lists.newArrayList();		
		if (response != null)
		{
			Document document = Jsoup.parse(response, "http://www.neworleans.com/mytrip/app");
			getAdditionalResultsAsync(params, request, document, initialResultsComplete);
			hotels = createHotelsNODC(params, document);
		}
		String cacheKey = createCacheKey(params);
		logger.debug(String.format("CACHE: nodc cache key (%1$s); num hotels stored: " + hotels.size(), cacheKey));
		request.getSession().setAttribute(InventorySource.NODC.name(), hotels);
		initialResultsComplete.countDown();
		logger.debug("nodc initial results complete (" + hotels.size() + ") hotels returned");
		return hotels;
	}

	private void getAdditionalResultsAsync(
			final SearchParams params, final HttpServletRequest request,
			Document initialResults, final CountDownLatch initialResultsComplete ) throws Exception
	{
		final List<Callable<Void>> workers = Lists.newArrayList();
		Element firstPaginator = initialResults.select("div.pagination").first();
		for (final Element link: firstPaginator.select("p.right span a[title*=Go to page]"))
		{
			workers.add(new Callable<Void>(){

				@Override
				public Void call() throws Exception
				{
					URIBuilder builder = new URIBuilder("http://www.neworleans.com"+link.attr("href"));
					/*
					builder
							.setScheme("http")
							.setHost("www.neworleans.com")
							.setPath("/mytrip/app")
							.addParameter("wicket:interface", link.attr("href").replace("?wicket:interface=", ""));
					*/
					String response = getResults(builder, params.getSessionInfo());
					if (response != null)
					{
						Document document = Jsoup.parse(response, "http://www.neworleans.com/mytrip/app");
						Collection<Hotel> asyncResult = createHotelsNODC(params, document);
						addToResults(params, request, asyncResult,  initialResultsComplete);
						logger.debug("asyncResult returned: " + (asyncResult != null ? asyncResult.size() : 0) + " addl results");
					}
					return null;
				}});
		}
		new Thread(new Runnable(){

			@Override
			public void run()
			{
				try
				{
					asyncResultsThreadPool.invokeAll(workers);
				} catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
			}}).start();
	}
	
	private Collection<Hotel> createHotelsNODC(SearchParams params, Document document) throws Exception
	{				
		Elements hotelIds = document.select("[name=preferredProductId]").first().select("option");		
		Elements searchResults = document.select(".searchResult");
		List<Hotel> hotels = Lists.newArrayList();
		for (Element searchResult: searchResults)
		{
			if (Thread.interrupted())
			{
				logger.warn("Stopping result gathering; thread has been cancelled");
				break;				
			}
			
			Element hName = searchResult.select(".productTitle a").first();	
			logger.error("parsing result for hotel: " + hName.ownText());
			Hotel hotel = new Hotel();
			
			String extHotelId = 
					hotelIds.select(String.format("option:containsOwn(%1$s)", hName.ownText())).first().val();
			SourceHotel sourceHotel = sourceHotelDAO
					.getByHotelId(extHotelId, com.burda.scraper.model.persisted.InventorySource.NODC);
			if (sourceHotel == null)
			{
				logger.warn("skipping hotel with id: " + extHotelId + ", name: " + hName.ownText());
				continue;
			}
			
			hotel.setName(sourceHotel.getHotelName());
			hotel.setHotelDetails(hotelDetailDAO.getHotelDetail(new HotelDetailCacheKey(sourceHotel.getHotelName())));
			hotel.setSource(sourceHotel);
						
			Elements roomTypeElements = searchResult.select("table.hotelResults");
		
			for (Element roomTypeEl: roomTypeElements.select("tbody tr.cyl-HotelRow"))
			{
				Element bookIt = roomTypeEl.select(".bookIt").first();
				Element avgNightlyRate = roomTypeEl.select("td.priceCol span:not(.originalRate)").first();
				Element roomTypeName = roomTypeEl.select("td.productCol a:eq(0)").first();
				Element promoDesc = roomTypeEl.select("td.productCol span.promo").first();
				Element totalPrice = roomTypeEl.select("td.priceCol p.totalLine span").first();
				
				RoomType roomType = new RoomType();
				roomType.bookItUrl = calculateBookItUrl(bookIt);
				roomType.avgNightlyRate = InventoryUtils.createMoney(avgNightlyRate.ownText());
				Elements avgNightlyOriginalRateEl = roomTypeEl.select("td.priceCol span.originalRate");
				if (avgNightlyOriginalRateEl != null && !avgNightlyOriginalRateEl.isEmpty())
				{
					roomType.avgNightlyOriginalRate = 
							InventoryUtils.createMoney(avgNightlyOriginalRateEl.first().ownText());
				}
				roomType.name = roomTypeName.ownText();
				if (promoDesc != null)
					roomType.promoDesc = promoDesc.ownText();
				roomType.totalPrice = InventoryUtils.createMoney(totalPrice.ownText());
				
				LocalDate currentDate = new LocalDate(
						STAY_DATE_FORMAT.parse(document.select("[name=departureDate]").first().val()));
				for (Element dailyRateEl: roomTypeEl.select("td.dayCol"))
				{
					BigDecimal price = InventoryUtils
							.createMoney(dailyRateEl.select("span:not(.originalRate)").first().ownText());
						
					DailyRate dr = new DailyRate();
					dr.date = currentDate.toDate();
					dr.price = price;
					Elements origPriceEl = dailyRateEl.select("span.originalRate");
					if (origPriceEl != null && !origPriceEl.isEmpty())
						dr.originalPrice = InventoryUtils.createMoney(origPriceEl.first().ownText());
					currentDate = currentDate.plusDays(1);
					roomType.dailyRates.add(dr);
				}
				
				hotel.addRoomType(roomType);
			}
			hotels.add(hotel);
		}	
		
		logger.error("returning " + hotels.size() + " initial results for nodc");
		return hotels;
	}
	
	private String calculateBookItUrl(Element bookIt)
	{
		String onclick = bookIt.attr("onclick");
		int beginIdx = onclick.indexOf("?");
		int endIdx = onclick.indexOf("'; }");
		
		return "http://www.neworleans.com/mytrip/app/Products/hotel?"+onclick.substring(beginIdx+1, endIdx);
	}	
	
	
	private static final String queryNODCHotelsViaHttpClient(SearchParams sp)
	{
				
		//URIBuilder builder = new URIBuilder();
		StringBuffer builder = new StringBuffer();
		builder
			.append("http://www.neworleans.com/mytrip/app/SearchWidget?");
		builder.append(sp.getSessionInfo().getWicketSearchPath());
		builder.append("&productType=HOTEL");
		builder.append("&promoId=");
		builder.append("&mo=");
		builder.append("&"+URLEncoder.encode("pdp:physicalDestination")+"=982");
		builder.append("&"+URLEncoder.encode("departureDate")+"="+URLEncoder.encode(STAY_DATE_FORMAT.format(sp.getCheckInDate().toDate())));
		builder.append("&"+URLEncoder.encode("returnDate")+"="+URLEncoder.encode(STAY_DATE_FORMAT.format(sp.getCheckOutDate().toDate())));
		builder.append("&"+URLEncoder.encode("numRooms")+"="+sp.getNumRooms());
		builder.append("&"+URLEncoder.encode("rl:0:ro:na")+"="+sp.getNumAdults1());
		builder.append("&"+URLEncoder.encode("rl:0:ro:ob:ob_body:nc")+"="+sp.getNumChildren1());
		builder.append("&"+URLEncoder.encode("rl:1:ro:na")+"="+sp.getNumAdults2());
		builder.append("&"+URLEncoder.encode("rl:1:ro:ob:ob_body:nc")+"="+sp.getNumChildren2());
		builder.append("&"+URLEncoder.encode("rl:2:ro:na")+"="+sp.getNumAdults3());
		builder.append("&"+URLEncoder.encode("rl:2:ro:ob:ob_body:nc")+"="+sp.getNumChildren3());
		builder.append("&"+URLEncoder.encode("rl:3:ro:na")+"="+sp.getNumAdults4());
		builder.append("&"+URLEncoder.encode("rl:3:ro:ob:ob_body:nc")+"="+sp.getNumChildren4());
		builder.append("&"+URLEncoder.encode("a:0:b:c:0:d:d_body:e")+"="+(sp.getNumChildren1() > 0 ? toS(sp.getRoom1ChildAge1()) : ""));
		builder.append("&"+URLEncoder.encode("a:0:b:c:1:d:d_body:e")+"="+(sp.getNumChildren1() > 1 ? toS(sp.getRoom1ChildAge2()) : ""));
		builder.append("&"+URLEncoder.encode("a:0:b:c:2:d:d_body:e")+"="+(sp.getNumChildren1() > 2 ? toS(sp.getRoom1ChildAge3()) : ""));
		builder.append("&"+URLEncoder.encode("a:1:b:c:0:d:d_body:e")+"="+(sp.getNumChildren2() > 0 ? toS(sp.getRoom2ChildAge1()) : ""));
		builder.append("&"+URLEncoder.encode("a:1:b:c:1:d:d_body:e")+"="+(sp.getNumChildren2() > 1 ? toS(sp.getRoom2ChildAge2()) : ""));
		builder.append("&"+URLEncoder.encode("a:1:b:c:2:d:d_body:e")+"="+(sp.getNumChildren2() > 2 ? toS(sp.getRoom2ChildAge3()) : ""));
		builder.append("&"+URLEncoder.encode("a:2:b:c:0:d:d_body:e")+"="+(sp.getNumChildren3() > 0 ? toS(sp.getRoom3ChildAge1()) : ""));
		builder.append("&"+URLEncoder.encode("a:2:b:c:1:d:d_body:e")+"="+(sp.getNumChildren3() > 1 ? toS(sp.getRoom3ChildAge2()) : ""));
		builder.append("&"+URLEncoder.encode("a:2:b:c:2:d:d_body:e")+"="+(sp.getNumChildren3() > 2 ? toS(sp.getRoom3ChildAge3()) : ""));
		builder.append("&"+URLEncoder.encode("a:3:b:c:0:d:d_body:e")+"="+(sp.getNumChildren4() > 0 ? toS(sp.getRoom4ChildAge1()) : ""));
		builder.append("&"+URLEncoder.encode("a:3:b:c:1:d:d_body:e")+"="+(sp.getNumChildren4() > 1 ? toS(sp.getRoom4ChildAge2()) : ""));
		builder.append("&"+URLEncoder.encode("a:3:b:c:2:d:d_body:e")+"="+(sp.getNumChildren4() > 2 ? toS(sp.getRoom4ChildAge3()) : ""));
		builder.append("&preferredProductId=");
		builder.append("&JSONFormSubmit=true");
		builder.append("&json=true");
		builder.append("&jsoncallback=jquery123");
		builder.append("&"+URLEncoder.encode("componentAsWidget_searchWidgetForm_hf_0")+"=");		
		
		
		
		
		
		
		
	/*	
				.setScheme("http")
				.setHost("www.neworleans.com")
				.setPath("/mytrip/app/SearchWidget")
				//.setFragment("?"+sp.getSessionInfo().getWicketSearchPath())
				.addParameter("", sp.getSessionInfo().getWicketSearchPath())
				.addParameter("productType", "HOTEL")
				.addParameter("promoId", "")
				.addParameter("mo", "")
				.addParameter("pdp:physicalDestination", "982")
				.addParameter("departureDate", STAY_DATE_FORMAT.format(sp.getCheckInDate().toDate()))
				.addParameter("returnDate", STAY_DATE_FORMAT.format(sp.getCheckOutDate().toDate()))
				.addParameter("numRooms", toS(sp.getNumRooms()))
				.addParameter("rl:0:ro:na", toS(sp.getNumAdults1()))
				.addParameter("rl:0:ro:ob:ob_body:nc", toS(sp.getNumChildren1()))
				.addParameter("rl:1:ro:na", toS(sp.getNumAdults2()))
				.addParameter("rl:1:ro:ob:ob_body:nc", toS(sp.getNumChildren2()))
				.addParameter("rl:2:ro:na", toS(sp.getNumAdults3()))
				.addParameter("rl:2:ro:ob:ob_body:nc", toS(sp.getNumChildren3()))
				.addParameter("rl:3:ro:na", toS(sp.getNumAdults4()))
				.addParameter("rl:3:ro:ob:ob_body:nc", toS(sp.getNumChildren4()))
				.addParameter("a:0:b:c:0:d:d_body:e", sp.getNumChildren1() > 0 ? toS(sp.getRoom1ChildAge1()) : "")
				.addParameter("a:0:b:c:1:d:d_body:e", sp.getNumChildren1() > 1 ? toS(sp.getRoom1ChildAge2()) : "")
				.addParameter("a:0:b:c:2:d:d_body:e", sp.getNumChildren1() > 2 ? toS(sp.getRoom1ChildAge3()) : "")
				.addParameter("a:1:b:c:0:d:d_body:e", sp.getNumChildren2() > 0 ? toS(sp.getRoom2ChildAge1()) : "")
				.addParameter("a:1:b:c:1:d:d_body:e", sp.getNumChildren2() > 1 ? toS(sp.getRoom2ChildAge2()) : "")
				.addParameter("a:1:b:c:2:d:d_body:e", sp.getNumChildren2() > 2 ? toS(sp.getRoom2ChildAge3()) : "")
				.addParameter("a:2:b:c:0:d:d_body:e", sp.getNumChildren3() > 0 ? toS(sp.getRoom3ChildAge1()) : "")
				.addParameter("a:2:b:c:1:d:d_body:e", sp.getNumChildren3() > 1 ? toS(sp.getRoom3ChildAge2()) : "")
				.addParameter("a:2:b:c:2:d:d_body:e", sp.getNumChildren3() > 2 ? toS(sp.getRoom3ChildAge3()) : "")
				.addParameter("a:3:b:c:0:d:d_body:e", sp.getNumChildren4() > 0 ? toS(sp.getRoom4ChildAge1()) : "")
				.addParameter("a:3:b:c:1:d:d_body:e", sp.getNumChildren4() > 1 ? toS(sp.getRoom4ChildAge2()) : "")
				.addParameter("a:3:b:c:2:d:d_body:e", sp.getNumChildren4() > 2 ? toS(sp.getRoom4ChildAge3()) : "")
				.addParameter("preferredProductId", "")
				.addParameter("JSONFormSubmit", "true")
				.addParameter("json", "true")
				.addParameter("jsoncallback", "jquery123")
				.addParameter("componentAsWidget_searchWidgetForm_hf_0", "");	
				*/	
		String results = null;
		try
		{
			results = getResults(new URIBuilder(builder.toString()), sp.getSessionInfo());	
		}
		catch (Exception e)
		{
			logger.error("Unable to build url", e);			
		}
		return results;
		
	}
	
	private static final String getResults(URIBuilder builder, SessionInfo sessionInfo)
	{
		String response = null;
		try
		{			
			HttpGet httpget = new HttpGet(builder.build());
			CookieStore cookieStore = new BasicCookieStore(); 
			
			if (StringUtils.isNotEmpty(sessionInfo.getSessionId()))
			{
				BasicClientCookie cookie = 
						new BasicClientCookie("JSESSIONID", sessionInfo.getSessionId());
				cookie.setPath("/");
				cookie.setDomain("www.neworleans.com");
				cookieStore.addCookie(cookie);
			}
			if (StringUtils.isNotEmpty(sessionInfo.getWWWSid()))
			{
				BasicClientCookie cookie = 
						new BasicClientCookie("www_sid", sessionInfo.getWWWSid());
				cookie.setPath("/");
				cookie.setDomain("www.neworleans.com");
				cookieStore.addCookie(cookie);				
			}			
			logger.error("uri == " + httpget.getURI());
			
			DefaultHttpClient httpClient = new DefaultHttpClient();
			httpClient.setCookieStore(cookieStore);
			httpget.setHeader("Accept", "text/javascript, application/javascript, application/ecmascript, application/x-ecmascript, */*; q=0.01");
			httpget.setHeader("X-Requested-With", "XMLHttpRequest");
			CustomRedirectStrategy redirectStrategy = new CustomRedirectStrategy();
			httpClient.setRedirectStrategy(redirectStrategy);
			HttpParams httpParams = httpClient.getParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, 45000);
	    HttpConnectionParams.setSoTimeout(httpParams, 45000);
	    
			HttpResponse httpResponse = httpClient.execute(httpget);
			if (redirectStrategy.getRetrievedHtml() != null)
				return redirectStrategy.getRetrievedHtml();
			else
				return EntityUtils.toString(httpResponse.getEntity());
		} 
		catch (Exception e)
		{
			logger.error("unable to retrieve query response", e);
		}
		return response;		
	}
	
	static final String findCookieValue(Object cookieStore, String cookieName)
	{
		String value = "";
		if (cookieStore != null)
		{
			if (cookieStore instanceof HttpServletRequest)
			{
				HttpServletRequest request = (HttpServletRequest)cookieStore;
				if (request.getCookies() != null)
				{
					for (Cookie c: request.getCookies())
					{
						logger.error("cookie submitted: " + c.getName() + ": " + c.getValue());
						if (c.getName().equals(cookieName))
						{
							value = c.getValue();
						}
					}			
				}				
			}
			else 
			{
				CookieStore httpClientCookieStore = (CookieStore)cookieStore;
				if (httpClientCookieStore.getCookies() != null)
				{
					for (org.apache.http.cookie.Cookie c: httpClientCookieStore.getCookies())
					{
						logger.error("cookie submitted: " + c.getName() + ": " + c.getValue());
						if (c.getName().equals(cookieName))
						{
							value = c.getValue();
						}
					}			
				}					
			}
		}
		return value;		
	}
	
	private static final String toS(int x)
	{
		return String.valueOf(x);
	}

	public void setSourceHotelDAO(SourceHotelDAO sourceHotelDAO)
	{
		this.sourceHotelDAO = sourceHotelDAO;
	}

	public void setHotelDetailDAO(HotelDetailDAO hotelDetailDAO)
	{
		this.hotelDetailDAO = hotelDetailDAO;
	}
	
	private void addToResults(
			SearchParams params, HttpServletRequest request, Collection<Hotel> hotels, CountDownLatch initialResultsComplete)
	{
		List<Hotel> existingHotelsInCache = Lists.newArrayList();
		synchronized(initialResultsComplete)
		{
			try
			{
				initialResultsComplete.await();
				existingHotelsInCache = (List<Hotel>)request.getSession().getAttribute(InventorySource.NODC.name());	
			}
			catch (Exception e)
			{
				logger.error("Unable to get current hotels from cache", e);
			}
			
			existingHotelsInCache.addAll(hotels);
			
			try
			{
				logger.debug(String.format(
						"CACHE: nodc cache key (%1$s); num hotels stored: " + hotels.size(), createCacheKey(params)));
				request.getSession().setAttribute(InventorySource.NODC.name(), existingHotelsInCache);
			}
			catch (Exception e)
			{
				logger.error("unable to update async results", e);
			}			
		}

	}
	
	private String createCacheKey(SearchParams params)
	{
		return params.getSessionInfo().getSessionId()+InventorySource.NODC.name();
	}
}