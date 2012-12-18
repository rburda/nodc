package com.burda.scraper.inventory;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.burda.scraper.model.Hotel;
import com.burda.scraper.model.RoomType;
import com.burda.scraper.model.SearchResult;

public class FrenchQuarterGuideXMLInventorySource implements InventorySource
{
	Logger logger = LoggerFactory.getLogger(FrenchQuarterGuideXMLInventorySource.class);
	
	@Override
	public SearchResult getResults() throws Exception
	{
		SearchResult result;
		HttpResponse resp = queryHotelsViaHttpClient();
		
		byte[] html = EntityUtils.toByteArray(resp.getEntity());
		result = createHotels(html);
		
		return result;
	}

	private SearchResult createHotels(byte[] xml) throws Exception
	{
		SearchResult result = new SearchResult();
		Document xmlResults = Jsoup.parse(new String(xml), "", Parser.xmlParser());
		
		Elements hotels = xmlResults.select("hotel");
		for (Element hotelEl: hotels)
		{
			Hotel hotel = new Hotel();
			hotel.name = hotelEl.select("hotel_name").first().ownText();
			hotel.areaDescription = hotelEl.select("district").first().ownText();
			hotel.description = hotelEl.select("description_full").first().ownText();
			//hotel.mapUrl = hotelEl.select("")
			//hotel.moreInfoUrl = hotelEl.select(")
			//hotel.photosUrl =
			hotel.source="FQG";
			List<RoomType> roomTypes = new ArrayList<RoomType>();	
		}
		return result;
	}
	
	//key==<hotelid>_chkIn_numNights_numRooms    ---> //s3_location
	
	private HttpResponse queryHotelsViaHttpClient()
	{
		URIBuilder builder = new URIBuilder();
		builder
				.setScheme("https")
				.setHost("secure.rezserver.com")
				.setPath("/api/hotel/getHotelData")
				.addParameter("api_key", "5f871629935ff113b876b4bcb1ca70e4")
				.addParameter("refid", "5057")
				.addParameter("hotel_id", "61150")
				.addParameter("check_in", "03/18/2013")
				.addParameter("check_out", "03/19/2013")
				.addParameter("rooms", "1");
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
