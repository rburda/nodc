package com.nodc.scraper.cache;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.nodc.scraper.dao.CacheStateDAO;
import com.nodc.scraper.dao.HotelDetailCacheKey;
import com.nodc.scraper.dao.HotelDetailDAO;
import com.nodc.scraper.dao.MasterHotelDAO;
import com.nodc.scraper.dao.SourceHotelDAO;
import com.nodc.scraper.inventory.InventoryUtils;
import com.nodc.scraper.model.persisted.HotelDetail;
import com.nodc.scraper.model.persisted.InventorySource;
import com.nodc.scraper.model.persisted.MasterHotel;
import com.nodc.scraper.model.persisted.SourceHotel;

@Component
public class OneTimeHotelLoader
{
	private static final Logger logger = LoggerFactory.getLogger(OneTimeHotelLoader.class);
	
	private final MasterHotelDAO masterHotelDAO;
	private final SourceHotelDAO sourceHotelDAO;
	private final HotelDetailDAO hotelDetailDAO;
	private final CacheStateDAO cacheStateDAO;
	
	@Autowired
	public OneTimeHotelLoader(
			MasterHotelDAO mhDAO, SourceHotelDAO shDAO, HotelDetailDAO hdDAO,
			CacheStateDAO csDAO)
	{
		this.masterHotelDAO = mhDAO;
		this.sourceHotelDAO = shDAO;
		this.hotelDetailDAO = hdDAO;
		this.cacheStateDAO = csDAO;
	}
			
	
	//1:02am every day; '0' to ensure ordering with other tasks; tasks are 
	//single threaded so if tasks overlap, they will wait for an executing one to
	//finish before starting the next one. This will be the first one to execute
	@Scheduled(cron = "0 0 1 * * ?")
	public void initializeHotelData() throws Exception
	{
		initializeHotelData(null);
	}
	
	public void initializeHotelData(String optionalSingleHotel) throws Exception
	{
		int page = 1;
		HttpResponse hotelSummaryResponse = queryHotelsViaHttpClient(page);
		byte[] xml = EntityUtils.toByteArray(hotelSummaryResponse.getEntity());
		Document xmlSummaryResults = Jsoup.parse(new String(xml), "", Parser.xmlParser());
		int totalPages = Integer.valueOf(xmlSummaryResults.select("hotel_data").first().select("total_pages").first().ownText());
		while (page <= totalPages)
		{
			logger.error("retrieving hotels for page: " + page);
			hotelSummaryResponse = queryHotelsViaHttpClient(page);
			xml = EntityUtils.toByteArray(hotelSummaryResponse.getEntity());
			xmlSummaryResults = Jsoup.parse(new String(xml), "", Parser.xmlParser());
			
			for (Element hotelEl: xmlSummaryResults.select("hotel_data").select("hotel"))
			{
				MasterHotel hotel = null;
				
				String hotelId = hotelEl.select("hotel_id").first().ownText();
				String hotelName = InventoryUtils.urlDecode(hotelEl.select("hotel_name").first().ownText());
				if (optionalSingleHotel != null)
					if (!hotelName.equals(optionalSingleHotel))
						continue;
				
				logger.error("init hotel id: " + hotelId);
				//check to see if we've seen this hotel before
				SourceHotel sourceHotel = 
						sourceHotelDAO.loadSourceHotelFromDB(hotelId, InventorySource.FQG);
				
				//if not, record it now.
				if (sourceHotel == null)
				{
					sourceHotel = new SourceHotel();
					sourceHotel.setHotelName(hotelName);
					sourceHotel.setExternalHotelId(hotelId);
					sourceHotel.setInvSource(InventorySource.FQG);
					sourceHotelDAO.save(sourceHotel);
					logger.error("hotel id: " + hotelId + " not found; creating sourceHotel with name of: " + sourceHotel.getHotelName());
				}
		
				//now see if we've created a master record before. This can only have
				//been true if the sourceHotel was not null
				hotel = masterHotelDAO.loadMasterHotelFromDB(sourceHotel.getHotelName());
				
				//if not go ahead and create one.
				if (hotel == null)
				{
					logger.error("master hotel not found for id: " + hotelId + "; creating master with name of: " + sourceHotel.getHotelName());
					hotel = new MasterHotel();
					hotel.setFavoredInventorySource(InventorySource.FQG);
					hotel.setWeight(1000);
					hotel.setHotelName(sourceHotel.getHotelName());
					hotel.setUuid(UUID.randomUUID().toString());
					masterHotelDAO.save(hotel);
				}
				else
					logger.error("master hotel found for id: " + hotelId + " with name of: " + sourceHotel.getHotelName());
			
				//lastly, check to see if we already have a content detail record.
				//If not, create one; if we do, then still set the address info as that
				//doesn't come in during the normal cache load
				HotelDetailCacheKey key = new HotelDetailCacheKey(hotel.getHotelName(), InventorySource.FQG);
				HotelDetail hd = hotelDetailDAO.loadHotelDetailFromDB(key);
				Map<String, Boolean> overrideMap = hotelDetailDAO.loadHotelDetailOverridesAsMapFromDB(key);
				boolean isNew = false;
				if (hd == null)
				{
					logger.error("hotel detail not found for id: " + hotelId + "; creating detail with name of: " + hotel.getHotelName());
					hd = new HotelDetail();
					hd.setName(hotel.getHotelName());
					isNew = true;
				}
				else
					logger.error("hotel detail found for id: " + hotelId + " with name of: " + hotel.getHotelName());
				
				if(FrenchQuarterGuideCacheLoader.isContentRefreshable(isNew, overrideMap, "address"))
					hd.setAddress1(InventoryUtils.urlDecode(hotelEl.select("address").first().ownText()));
				if(FrenchQuarterGuideCacheLoader.isContentRefreshable(isNew, overrideMap, "city"))
					hd.setCity(InventoryUtils.urlDecode(hotelEl.select("city_name").first().ownText()));
				if(FrenchQuarterGuideCacheLoader.isContentRefreshable(isNew, overrideMap, "state"))
					hd.setState(hotelEl.select("state_code").first().ownText());
				
				if(FrenchQuarterGuideCacheLoader.isContentRefreshable(isNew, overrideMap, "address"))
				{
					hd.setAddress1(StringUtils.removeEnd(hd.getAddress1(), hd.getCity()));
					hd.setAddress1(StringUtils.removeEnd(hd.getAddress1(),  ", "));
				}

				if(FrenchQuarterGuideCacheLoader.isContentRefreshable(isNew, overrideMap, "city"))
				{				
					hd.setCity(StringUtils.removeEnd(hd.getCity(), hd.getState()));
					hd.setCity(StringUtils.removeEnd(hd.getCity(), ", "));
				}
				hotelDetailDAO.save(hd);
			}
			page++;
		}
		cacheStateDAO.markHotelDetailCacheUpdated();
		cacheStateDAO.markRoomTypeCacheUpdated();
	}
	
	private HttpResponse queryHotelsViaHttpClient(int page)
	{
		URIBuilder builder = new URIBuilder();
		/*
		builder
				.setScheme("http")
				.setHost("api.rezserver.com")
				.setPath("/api/hotel/getStaticHotels")
				.addParameter("refid", "5057")
				.addParameter("limit", "300")
				.addParameter("api_key", "5f871629935ff113b876b4bcb1ca70e4")
				.addParameter("airport_code", "MSY")
				.addParameter("city_id", "3000008434");
		*/
		builder
		.setScheme("http")
		.setHost("api.rezserver.com")
		.setPath("/api/hotel/getResults")
		.addParameter("refid", "5057")
		.addParameter("limit", "600")
		.addParameter("api_key", "5f871629935ff113b876b4bcb1ca70e4")
		.addParameter("filter_add_radius", "30")
		.addParameter("page", String.valueOf(page))
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
}
