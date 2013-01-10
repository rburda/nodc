package com.burda.scraper.inventory;

import java.math.BigDecimal;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
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
import com.burda.scraper.model.SearchResult;
import com.burda.scraper.model.persisted.SourceHotel;
import com.google.common.collect.Lists;

public class NODCWarehouse implements Warehouse
{
	private static final DateFormat STAY_DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy");
	private static final Logger logger = LoggerFactory.getLogger(NODCWarehouse.class);
	
	@Autowired
	@Qualifier("sourceHotelDAO")
	private SourceHotelDAO sourceHotelDAO;
	
	@Autowired
	@Qualifier("hotelDetailDAO")
	private HotelDetailDAO hotelDetailDAO;
	
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
		
		HttpResponse httpResponse = queryNODCHotelsViaHttpClient("", sp);
		byte[] html = EntityUtils.toByteArray(httpResponse.getEntity());
		Document document = Jsoup.parse(new String(html), "http://www.neworleans.com/mytrip/app");
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
	public SearchResult getResults(HttpServletRequest request, SearchParams params) throws Exception
	{
		HttpResponse response = queryNODCHotelsViaHttpClient(findSessionCookieString(request), params);

		SearchResult result = new SearchResult(params);		
		if (response != null)
		{
			byte[] html = EntityUtils.toByteArray(response.getEntity());
			result = createHotelsNODC(params, html);			
		}
		logger.debug("nodc complete");
		return result;
	}
	
	private SearchResult createHotelsNODC(SearchParams params, byte[] html) throws Exception
	{
		SearchResult result = new SearchResult(params);
		
		Document document = Jsoup.parse(new String(html), "http://www.neworleans.com/mytrip/app");
		result.startDate = STAY_DATE_FORMAT.parse(document.select("[name=departureDate]").first().val());
		result.endDate = STAY_DATE_FORMAT.parse(document.select("[name=returnDate]").first().val());
		
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
				
				LocalDate currentDate = new LocalDate(result.startDate);
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
		
		result.setAllHotels(hotels);
		return result;
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
	
	
	private static final HttpResponse queryNODCHotelsViaHttpClient(String sessionCookieString, SearchParams sp)
	{
		URIBuilder builder = new URIBuilder();
		builder
				.setScheme("http")
				.setHost("www.neworleans.com")
				.setPath("/mytrip/app/SearchWidget")
				.addParameter("id88_hf_0", "")
				.addParameter("productType", "HOTEL")
				.addParameter("promoId", "")
				.addParameter("mo", "")
				.addParameter("pdp:physicalDestination", "982")
				.addParameter("departureDate", STAY_DATE_FORMAT.format(sp.getCheckInDate().toDate()))
				.addParameter("returnDate", STAY_DATE_FORMAT.format(sp.getCheckOutDate().toDate()))
				.addParameter("numRooms", toS(sp.getNumRooms()))
				.addParameter("r1:0:ro:na", toS(sp.getNumAdults1()))
				.addParameter("rl:0:ro:ob:nc", toS(sp.getNumChildren1()))
				.addParameter("rl:1:ro:na", toS(sp.getNumAdults2()))
				.addParameter("rl:1:ro:ob:nc", toS(sp.getNumChildren2()))
				.addParameter("rl:2:ro:na", toS(sp.getNumAdults3()))
				.addParameter("rl:2:ro:ob:nc", toS(sp.getNumChildren3()))
				.addParameter("rl:3:ro:na", toS(sp.getNumAdults4()))
				.addParameter("rl:3:ro:ob:nc", toS(sp.getNumChildren4()))
				.addParameter("a:0:b:c:0:d:e", toS(sp.getRoom1ChildAge1()))
				.addParameter("a:0:b:c:1:d:e", toS(sp.getRoom1ChildAge2()))
				.addParameter("a:0:b:c:2:d:e", toS(sp.getRoom1ChildAge3()))
				.addParameter("a:1:b:c:0:d:e", toS(sp.getRoom2ChildAge1()))
				.addParameter("a:1:b:c:1:d:e", toS(sp.getRoom2ChildAge2()))
				.addParameter("a:1:b:c:2:d:e", toS(sp.getRoom2ChildAge3()))
				.addParameter("a:2:b:c:0:d:e", toS(sp.getRoom3ChildAge1()))
				.addParameter("a:2:b:c:1:d:e", toS(sp.getRoom3ChildAge2()))
				.addParameter("a:2:b:c:2:d:e", toS(sp.getRoom3ChildAge3()))
				.addParameter("a:3:b:c:0:d:e", toS(sp.getRoom4ChildAge1()))
				.addParameter("a:3:b:c:1:d:e", toS(sp.getRoom4ChildAge2()))
				.addParameter("a:3:b:c:2:d:e", toS(sp.getRoom4ChildAge3()))
				.addParameter("preferredProductId", "")
				.addParameter("wicket:bookmarkablePage",
						":com.vegas.athena.components.browse.hotel.HotelBrowsePage")
				;					
		HttpResponse response = null;
		try
		{			
			URI uri = builder.build();
			HttpPost httpget = new HttpPost(uri);
			httpget.setHeader("Cookie", sessionCookieString);
			
			logger.error("uri == " + httpget.getURI());
			
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpParams httpParams = httpClient.getParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, 45000);
	    HttpConnectionParams.setSoTimeout(httpParams, 45000);
			response = httpClient.execute(httpget);
		} 
		catch (Exception e)
		{
			logger.error("unable to retrieve query response", e);
		}
		return response;
	}
	
	private static final String findSessionCookieString(HttpServletRequest request)
	{
		String sessionCookieString = "";
		if (request != null && request.getCookies() != null)
		{
			for (Cookie c: request.getCookies())
			{
				if (c.getName().equals("parent_cookie"))
				{
					sessionCookieString = c.getValue().replace("___", ";");
					break;
				}
			}			
		}
		return sessionCookieString;
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
	
	
}