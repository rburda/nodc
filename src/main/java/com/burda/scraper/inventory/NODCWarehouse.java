package com.burda.scraper.inventory;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;
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
import com.burda.scraper.model.DailyRate;
import com.burda.scraper.model.Hotel;
import com.burda.scraper.model.RoomType;
import com.burda.scraper.model.SearchParams;
import com.burda.scraper.model.persisted.InventorySource;
import com.burda.scraper.model.persisted.SourceHotel;
import com.google.code.ssm.api.format.SerializationType;
import com.google.common.collect.Lists;

public class NODCWarehouse implements Warehouse
{
	private static final DateFormat STAY_DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy");
	private static final Logger logger = LoggerFactory.getLogger(NODCWarehouse.class);
	
	private static final ExecutorService asyncResultsThreadPool = Executors.newCachedThreadPool();
	
	private static final class SessionInfo
	{
		final String jsessionId;
		final String wwwsid;
		final String wicketSessionPathForSearch;
		
		private SessionInfo()
		{
			//TODO: create a session
			this.jsessionId = "";
			this.wwwsid = "";
			this.wicketSessionPathForSearch = "";
		}
		
		private SessionInfo(HttpServletRequest servletRequest)
		{
			String cValue = findCookieValue(servletRequest, "parent_jsession_id");
			if (cValue.indexOf("=") >= 0)
				cValue = cValue.substring(cValue.indexOf("=")+1, cValue.length());
			jsessionId = cValue;
			
			cValue = findCookieValue(servletRequest, "parent_sid");
			if (cValue.indexOf("=") >= 0)
				cValue = cValue.substring(cValue.indexOf("=")+1, cValue.length());
			wwwsid = cValue;
			
			cValue = findCookieValue(servletRequest, "parent_url");
			if (cValue.indexOf("=") >= 0)
				cValue = cValue.substring(cValue.indexOf("=")+1, cValue.length());
			wicketSessionPathForSearch = cValue;
		}
	}
	
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
	
  @Autowired
  @Qualifier("defaultMemcachedClient") 
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
		
		SearchParams sp = SearchParams.oneRoomOneAdult(
				new LocalDate().plusMonths(1), new LocalDate().plusMonths(1).plusDays(2));		
		
		String response = queryNODCHotelsViaHttpClient(new SessionInfo(), sp);
		Document document = Jsoup.parse(response, "http://www.neworleans.com/mytrip/app");
		for (Element hotelEl: document.select("[name=preferredProductId]").first().select("option"))
		{
			if (hotelEl.ownText().equalsIgnoreCase("All Hotels"))
				continue;
			
			Hotel h = new Hotel();
			SourceHotel source = new SourceHotel();
			source.setExternalHotelId(hotelEl.val());
			source.setHotelName(hotelEl.ownText());
			source.setInvSource(com.burda.scraper.model.persisted.InventorySource.NODC);
			h.setSource(source);
			
			hotels.add(h);
		}
		
		return hotels;
	}
	
	@Override
	public Collection<Hotel> 
		getInitialResultsAndAsyncContinue(HttpServletRequest request, SearchParams params) throws Exception
	{
		CountDownLatch initialResultsComplete = new CountDownLatch(1);
		SessionInfo sessionInfo = new SessionInfo(request);
		String response = queryNODCHotelsViaHttpClient(sessionInfo, params);

		Collection<Hotel> hotels = Lists.newArrayList();		
		if (response != null)
		{
			Document document = Jsoup.parse(response, "http://www.neworleans.com/mytrip/app");
			getAdditionalResultsAsync(sessionInfo, params, document, initialResultsComplete);
			hotels = createHotelsNODC(params, document);
		}
		cache.set(createCacheKey(params), (60*180), hotels, SerializationType.JSON);
		initialResultsComplete.countDown();
		logger.debug("nodc complete");
		return hotels;
	}

	private void getAdditionalResultsAsync(
			final SessionInfo sessionInfo, final SearchParams params, 
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
					URIBuilder builder = new URIBuilder();
					builder
							.setScheme("http")
							.setHost("www.neworleans.com")
							.setPath("/mytrip/app")
							.addParameter("wicket:interface", link.attr("href").replace("?wicket:interface=", ""));

					String response = getResults(builder, sessionInfo);
					if (response != null)
					{
						Document document = Jsoup.parse(response, "http://www.neworleans.com/mytrip/app");
						addToResults(params, createHotelsNODC(params, document), initialResultsComplete);
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
			//Element hDescription = searchResult.select(".productSummary p:eq(1)").first();	
			//Element hArea = searchResult.select(".productSummary p:eq(0)").first();
			//Element hMapUrl = searchResult.select(".productSummary p:eq(0) a").first();
			//Element hMoreInfoUrl = searchResult.select(".productSummary p:eq(2) a:eq(0)").first();
			//Element hPhotosUrl = searchResult.select(".productSummary p:eq(0) a:eq(1)").first();
			
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
		
		return hotels;
	}
	
	private String calculateStandardUrl(Element el)
	{
		String onclick = el.attr("onclick");
		int beginIdx = onclick.indexOf("?");
		int endIdx = onclick.indexOf("',");
		
		return onclick.substring(beginIdx+1, endIdx);			
	}
	
	private String calculatePhotosUrl(Element photoUrl)
	{
		String onclick = photoUrl.attr("onclick");
		int beginIdx = onclick.indexOf("?");
		int endIdx = onclick.indexOf("',");
		
		return onclick.substring(beginIdx+1, endIdx);			
	}
	
	private String calculateMoreInfoUrl(Element moreInfoUrl)
	{
		String onclick = moreInfoUrl.attr("onclick");
		int beginIdx = onclick.indexOf("?");
		int endIdx = onclick.indexOf("',");
		
		return onclick.substring(beginIdx+1, endIdx);			
	}
	
	private String calculateMapUrl(Element mapUrl)
	{
		String onclick = mapUrl.attr("onclick");
		int beginIdx = onclick.indexOf("?");
		int endIdx = onclick.indexOf("',");
		
		return onclick.substring(beginIdx+1, endIdx);		
	}
	
	private String calculateArea(Element area)
	{
		int beginIdx = area.ownText().indexOf("Area:");
		int endIdx = area.ownText().indexOf("(");
		
		return area.ownText().substring(beginIdx, endIdx);
	}
	
	private String calculateBookItUrl(Element bookIt)
	{
		String onclick = bookIt.attr("onclick");
		int beginIdx = onclick.indexOf("?");
		int endIdx = onclick.indexOf("'; }");
		
		return "http://www.neworleans.com/mytrip/app?"+onclick.substring(beginIdx+1, endIdx);
	}	
	
	
	private static final String queryNODCHotelsViaHttpClient(SessionInfo sessionInfo, SearchParams sp)
	{
		URIBuilder builder = new URIBuilder();
		builder
				.setScheme("http")
				.setHost("www.neworleans.com")
				.setPath("/mytrip/app/SearchWidget/")
				.addParameter("wicket:interface", sessionInfo.wicketSessionPathForSearch)
				.addParameter("productType", "HOTEL")
				.addParameter("promoId", "")
				.addParameter("mo", "")
				.addParameter("pdp:physicalDestination", "982")
				.addParameter("departureDate", STAY_DATE_FORMAT.format(sp.getCheckInDate().toDate()))
				.addParameter("returnDate", STAY_DATE_FORMAT.format(sp.getCheckOutDate().toDate()))
				.addParameter("numRooms", toS(sp.getNumRooms()))
				.addParameter("rl:0:ro:na", toS(sp.getNumAdults1()))
				.addParameter("rl:0:ro:ob:nc", toS(sp.getNumChildren1()))
				.addParameter("rl:1:ro:na", toS(sp.getNumAdults2()))
				.addParameter("rl:1:ro:ob:nc", toS(sp.getNumChildren2()))
				.addParameter("rl:2:ro:na", toS(sp.getNumAdults3()))
				.addParameter("rl:2:ro:ob:nc", toS(sp.getNumChildren3()))
				.addParameter("rl:3:ro:na", toS(sp.getNumAdults4()))
				.addParameter("rl:3:ro:ob:nc", toS(sp.getNumChildren4()))
				.addParameter("a:0:b:c:0:d:e", sp.getNumChildren1() > 0 ? toS(sp.getRoom1ChildAge1()) : "")
				.addParameter("a:0:b:c:1:d:e", sp.getNumChildren1() > 1 ? toS(sp.getRoom1ChildAge2()) : "")
				.addParameter("a:0:b:c:2:d:e", sp.getNumChildren1() > 2 ? toS(sp.getRoom1ChildAge3()) : "")
				.addParameter("a:1:b:c:0:d:e", sp.getNumChildren2() > 0 ? toS(sp.getRoom2ChildAge1()) : "")
				.addParameter("a:1:b:c:1:d:e", sp.getNumChildren2() > 1 ? toS(sp.getRoom2ChildAge2()) : "")
				.addParameter("a:1:b:c:2:d:e", sp.getNumChildren2() > 2 ? toS(sp.getRoom2ChildAge3()) : "")
				.addParameter("a:2:b:c:0:d:e", sp.getNumChildren3() > 0 ? toS(sp.getRoom3ChildAge1()) : "")
				.addParameter("a:2:b:c:1:d:e", sp.getNumChildren3() > 1 ? toS(sp.getRoom3ChildAge2()) : "")
				.addParameter("a:2:b:c:2:d:e", sp.getNumChildren3() > 2 ? toS(sp.getRoom3ChildAge3()) : "")
				.addParameter("a:3:b:c:0:d:e", sp.getNumChildren4() > 0 ? toS(sp.getRoom4ChildAge1()) : "")
				.addParameter("a:3:b:c:1:d:e", sp.getNumChildren4() > 1 ? toS(sp.getRoom4ChildAge2()) : "")
				.addParameter("a:3:b:c:2:d:e", sp.getNumChildren4() > 2 ? toS(sp.getRoom4ChildAge3()) : "")
				.addParameter("preferredProductId", "")
				.addParameter("JSONFormSubmit", "true")
				.addParameter("json", "true")
				.addParameter("jsoncallback", "jquery123")
				.addParameter("componentAsWidget_searchWidgetForm_hf_0", "");				
		return getResults(builder, sessionInfo);
	}
	
	private static final String getResults(URIBuilder builder, SessionInfo sessionInfo)
	{
		String response = null;
		try
		{			
			HttpGet httpget = new HttpGet(builder.build());
			CookieStore cookieStore = new BasicCookieStore(); 
			
			if (StringUtils.isNotEmpty(sessionInfo.jsessionId))
			{
				BasicClientCookie cookie = 
						new BasicClientCookie("JSESSIONID", sessionInfo.jsessionId);
				cookie.setPath("/");
				cookie.setDomain("www.neworleans.com");
				cookieStore.addCookie(cookie);
			}
			if (StringUtils.isNotEmpty(sessionInfo.wwwsid))
			{
				BasicClientCookie cookie = 
						new BasicClientCookie("www_sid", sessionInfo.wwwsid);
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
	
	private static final String findCookieValue(HttpServletRequest request, String cookieName)
	{
		String value = "";
		if (request != null && request.getCookies() != null)
		{
			for (Cookie c: request.getCookies())
			{
				if (c.getName().equals(cookieName))
				{
					value = c.getValue();
					break;
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
			SearchParams params, Collection<Hotel> hotels, CountDownLatch initialResultsComplete)
	{
		String cacheKey = createCacheKey(params);
		List<Hotel> existingHotelsInCache = Lists.newArrayList();
		synchronized(initialResultsComplete)
		{
			try
			{

				initialResultsComplete.await();
				existingHotelsInCache = cache.get(cacheKey, SerializationType.JSON);	
			}
			catch (Exception e)
			{
				logger.error("Unable to get current hotels from cache", e);
			}
			
			existingHotelsInCache.addAll(hotels);
			
			try
			{
				cache.set(cacheKey, (60*180), existingHotelsInCache, SerializationType.JSON);
			}
			catch (Exception e)
			{
				logger.error("unable to update async results", e);
			}			
		}

	}
	
	private String createCacheKey(SearchParams params)
	{
		return params.getSessionId()+InventorySource.NODC.name();
	}
}