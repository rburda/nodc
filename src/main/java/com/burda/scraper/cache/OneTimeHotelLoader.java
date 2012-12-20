package com.burda.scraper.cache;

import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.burda.scraper.dao.HotelDetailDAO;
import com.burda.scraper.dao.MasterHotelDAO;
import com.burda.scraper.dao.SourceHotelDAO;
import com.burda.scraper.model.persisted.HotelDetail;
import com.burda.scraper.model.persisted.InventorySource;
import com.burda.scraper.model.persisted.MasterHotel;
import com.burda.scraper.model.persisted.SourceHotel;

public class OneTimeHotelLoader
{
	private static final Logger logger = LoggerFactory.getLogger(OneTimeHotelLoader.class);
	
	private MasterHotelDAO masterHotelDAO;
	private SourceHotelDAO sourceHotelDAO;
	private HotelDetailDAO hotelDetailDAO;
	
	public void initializeHotelData() throws Exception
	{
		HttpResponse hotelSummaryResponse = queryHotelsViaHttpClient();
		byte[] xml = EntityUtils.toByteArray(hotelSummaryResponse.getEntity());
		Document xmlSummaryResults = Jsoup.parse(new String(xml), "", Parser.xmlParser());
		for (Element hotelEl: xmlSummaryResults.select("hotel_data").select("hotel"))
		{
			MasterHotel hotel = null;
			
			String hotelId = hotelEl.select("hotel_id").first().ownText();
			String extHotelName = hotelEl.select("hotel_name").first().ownText();
			
			//check to see if we've seen this hotel before
			SourceHotel sourceHotel = 
					sourceHotelDAO.getByHotelId(hotelId, InventorySource.FQG);
			
			//if not, record it now.
			if (sourceHotel == null)
			{
				sourceHotel = new SourceHotel();
				sourceHotel.setHotelName(extHotelName);
				sourceHotel.setExternalHotelId(hotelId);
				sourceHotel.setInvSource(InventorySource.FQG);
				sourceHotelDAO.save(sourceHotel);
			}
	
			//now see if we've created a master record before. This can only have
			//been true if the sourceHotel was not null
			hotel = masterHotelDAO.getByHotelName(sourceHotel.getHotelName());
			
			//if not go ahead and create one.
			if (hotel == null)
			{
				hotel = new MasterHotel();
				hotel.setFavoredInventorySource(InventorySource.FQG);
				hotel.setWeight(1000);
				hotel.setHotelName(extHotelName);
				masterHotelDAO.save(hotel);
			}
		
			//lastly, check to see if we already have a content detail record.
			//If not, create one; if we do, then still set the address info as that
			//doesn't come in during the normal cache load
			HotelDetail hd = hotelDetailDAO.getHotelDetail(hotel.getHotelName());
			if (hd == null)
			{
				hd = new HotelDetail();
				hd.setName(hotel.getHotelName());
			}
			hd.setAddress1(hotelEl.select("hotel_address").first().ownText());
			hd.setCity(hotelEl.select("city_name").first().ownText());
			hd.setState(hotelEl.select("state_code").first().ownText());
			hotelDetailDAO.save(hd);
		}
	}
	
	private HttpResponse queryHotelsViaHttpClient()
	{
		URIBuilder builder = new URIBuilder();
		builder
				.setScheme("http")
				.setHost("api.rezserver.com")
				.setPath("/api/hotel/getStaticHotels")
				.addParameter("refid", "5057")
				.addParameter("limit", "300")
				.addParameter("api_key", "5f871629935ff113b876b4bcb1ca70e4")
				.addParameter("city_id", "3000008434");
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
	
	public void setSourceHotelDAO(SourceHotelDAO sDAO)
	{
		this.sourceHotelDAO = sDAO;
	}
	
	public void setHotelDetailDAO(HotelDetailDAO hdDAO)
	{
		this.hotelDetailDAO = hdDAO;
	}
	
	public void setMasterHotelDAO(MasterHotelDAO dao)
	{
		this.masterHotelDAO = dao;
	}
}
