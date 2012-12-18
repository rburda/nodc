package com.burda.scraper.inventory;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.burda.scraper.model.Hotel;
import com.burda.scraper.model.RoomType;
import com.burda.scraper.model.SearchResult;

public class FrenchQuarterGuideInventorySource implements InventorySource
{
	private static final Logger logger = LoggerFactory.getLogger(FrenchQuarterGuideInventorySource.class);	
	
	@Override
	public SearchResult getResults() throws Exception
	{
		SearchResult result;
		HttpResponse resp = queryHotelsViaHttpClient();
		
		byte[] html = EntityUtils.toByteArray(resp.getEntity());
		result = createHotels(html);
		
		for (Header header: resp.getAllHeaders())
		{
			com.burda.scraper.model.Header newHeader = new com.burda.scraper.model.Header();			
			if (header.getValue().contains("JSESSIONID"))
			{
				newHeader.name = header.getName();
				newHeader.value =  header.getValue() + "; domain=.rezserver.com";
			}
			else
			{
				newHeader.name = header.getName();
				newHeader.value = header.getValue();	
			}
			result.headers.add(newHeader);
		}
		return result;
	}
	
	private SearchResult createHotels(byte[] html) throws Exception
	{
		Document document = Jsoup.parse(
				new String(html), "http://secure.rezserver.com/js/ajax/city_page_redesign/getResults.php");
		
		SearchResult result = new SearchResult();
		for (Element hotelElement: document.select(".hotelbox"))
		{
			Hotel hotel = new Hotel();
			hotel.source = "FQG";
			hotel.name = hotelElement.select(".hotel h1 a").first().ownText();
			hotel.photosUrl = hotelElement.select(".hotelbox_content .hotel_img").first().attr("src");
			for (Element rtElement: hotelElement.select(".room"))
			{
				RoomType rt = new RoomType();
				String onclick = rtElement.select(".room_price").first().attr("onclick");
				int begIdx = onclick.indexOf("'");
				int endIdx = onclick.indexOf("');");
				String[] idParts = onclick.substring(begIdx, endIdx).split(",");
				StringBuffer roomTypeId = new StringBuffer();
				for (int i=0; i < idParts.length; i++)
				{
					String idPart = StringUtils.strip(idParts[i], "'");
					idPart = StringUtils.strip(idPart, "'");
					idPart = StringUtils.strip(idPart, ")");
					idParts[i] = idPart;
					roomTypeId.append(idPart);
					if (i+1 != idParts.length)
						roomTypeId.append("_");
				}
				rt.name = rtElement.select(".roomlink .room_type a").first().ownText();
				rt.avgNightlyRate = InventoryUtils.createMoney(
						hotelElement.select("#rateDetails_"+roomTypeId + " .price").first().ownText());
				rt.totalPrice = InventoryUtils.createMoney(rtElement.select(".room_price .price").first().ownText());
				
				rt.bookItUrl=createBookUrl(idParts);
				hotel.roomTypes.add(rt);
			}
			result.hotels.add(hotel);
		}
		
		return result;
	}
	
	private String createBookUrl(String[] idParts)
	{
		StringBuffer url = new StringBuffer();
		url.append("https://secure.rezserver.com/book/index.php?refid=5057&seshid=ac5711115d836fd9a54f2c0757438cfc");
		url.append("refid=5057");
		url.append("&rs_hid="+idParts[0]);
		url.append("&rs_rate_cat="+idParts[1]);
		url.append("&rs_rate_code="+idParts[2]);
		url.append("&rs_room_code="+idParts[3]);
		url.append("&rs_city=New Orleans, LA");
		url.append("&rs_chk_in=12/14/2012");
		url.append("&rs_chk_out=12/16/2012");
		url.append("&rs_rooms=1");
		url.append("&ts_testing=");
		url.append("&_booknow=1");
		return url.toString();
	}
	

	private HttpResponse queryHotelsViaHttpClient()
	{
		URIBuilder builder = new URIBuilder();
		builder
				.setScheme("http")
				.setHost("secure.rezserver.com")
				.setPath("/js/ajax/city_page_redesign/getResults.php")
				.addParameter("rs_city", "New Orleans, Lousiana")
				.addParameter("rs_cid", "3000008434")
				.addParameter("rs_chk_in", "12/14/2012")
				.addParameter("rs_chk_out", "12/16/2012")
				.addParameter("rs_rooms", "1")
				.addParameter("rs_curr_code", "")
				.addParameter("rs_m_km", "")
				.addParameter("needLiveRates", "true")
				.addParameter("rs_page", "1")
				.addParameter("rs_sort", "mp")
				.addParameter("refid", "5057")
				.addParameter("disableCache","");
		HttpResponse response = null;
		try
		{
			URI uri = builder.build();
			HttpGet httpget = new HttpGet(uri);
			logger.error("uri == " + httpget.getURI());

			response = new DefaultHttpClient().execute(httpget);
		} 
		catch (Exception e)
		{
			logger.error("unable to retrieve query response", e);
		}
		return response;
	}	
	
}
