package com.nodc.scraper.cache;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.nodc.scraper.dao.CacheStateDAO;
import com.nodc.scraper.dao.HotelDetailCacheKey;
import com.nodc.scraper.dao.HotelDetailDAO;
import com.nodc.scraper.dao.MasterHotelDAO;
import com.nodc.scraper.dao.RoomTypeDetailDAO;
import com.nodc.scraper.dao.SourceHotelDAO;
import com.nodc.scraper.inventory.NODCWarehouse;
import com.nodc.scraper.model.Hotel;
import com.nodc.scraper.model.persisted.CacheState;
import com.nodc.scraper.model.persisted.HotelDetail;
import com.nodc.scraper.model.persisted.InventorySource;
import com.nodc.scraper.model.persisted.MasterHotel;
import com.nodc.scraper.model.persisted.RoomTypeDetail;
import com.nodc.scraper.model.persisted.SourceHotel;

@Component
public class NODCHotelLoader
{
	private static final Logger logger = LoggerFactory.getLogger(NODCHotelLoader.class);
	
	private final NODCWarehouse invSource;
	private final SourceHotelDAO sourceHotelDAO;
	private final MasterHotelDAO masterHotelDAO;
	private final HotelDetailDAO hotelDetailDAO;
	private final RoomTypeDetailDAO roomTypeDetailDAO;
	private final CacheStateDAO cacheStateDAO;
	
	@Autowired
	public NODCHotelLoader(
			NODCWarehouse whs, SourceHotelDAO shDAO, MasterHotelDAO mhDAO, 
			HotelDetailDAO hdDAO, RoomTypeDetailDAO rtdDAO, CacheStateDAO csDAO)
	{
		this.invSource = whs;
		this.sourceHotelDAO = shDAO;
		this.masterHotelDAO = mhDAO;
		this.hotelDetailDAO = hdDAO;
		this.roomTypeDetailDAO = rtdDAO;
		this.cacheStateDAO = csDAO;
	}
	
	public void updateWeights(Map<String, Integer> weights)
	{
		for (String hotelName: weights.keySet())
		{
			MasterHotel h = masterHotelDAO.loadMasterHotelFromDB(hotelName);
			if (h != null)
			{
				h.setWeight(weights.get(hotelName));
				masterHotelDAO.save(h);
			}
		}
	}
	
	//1:02am every day; '2' to ensure ordering with other tasks; tasks are 
	//single threaded so if tasks overlap, they will wait for an executing one to
	//finish before starting the next one. Last one to execute. Want this to 
	//execute last because we like NODC content more than priceline
	@Scheduled(cron = "0 2 1 * * ?") 
	public void loadCache() throws Exception
	{
		loadCache(null);
	}
	
	public void loadCache(String optionalSingleHotel) throws Exception
	{
		List<Hotel> shellHotels = invSource.getAllShellHotels();
		
		for (Hotel newHotel: shellHotels)
		{
			if (optionalSingleHotel != null)
				if (!newHotel.getName().equals(optionalSingleHotel))
					continue;
			
			logger.error(String.format("saving hotel: %1$s (%2$s)", newHotel.getName(), newHotel.getSource().getExternalHotelId()));
			String hotelId = newHotel.getSource().getExternalHotelId();
			
			//check to see if we've seen this hotel before (on the NODC side)
			SourceHotel previouslyFound = 
					sourceHotelDAO.loadSourceHotelFromDB(hotelId, InventorySource.NODC);
			//if we've never seen this hotel before
			if (previouslyFound == null)
			{
				logger.error("hotel not previously found");
				if (newHotel.getSource().getHotelName().contains("Harrah"))
				{
					newHotel.getSource().setHotelName(newHotel.getSource().getHotelName().replace("'",""));
				}
				sourceHotelDAO.save(newHotel.getSource());				
				
				//now check to see if a masterHotel has been created with this name
				//previously
				MasterHotel masterHotel = masterHotelDAO.loadMasterHotelFromDB(newHotel.getSource().getHotelName());
				if (masterHotel == null)
				{
					masterHotel = new MasterHotel();
					masterHotel.setHotelName(newHotel.getSource().getHotelName());
					masterHotel.setWeight(1000);
					masterHotel.setUuid(UUID.randomUUID().toString());
				}
				masterHotel.setFavoredInventorySource(InventorySource.NODC);
				masterHotelDAO.save(masterHotel);
			}
			else
			{
				logger.error("hotel previously found");
				newHotel.setSource(previouslyFound);
			}
			newHotel.setName(newHotel.getSource().getHotelName());
			newHotel.getHotelDetails().setName(newHotel.getSource().getHotelName());
			if (newHotel.getHotelDetails().getRoomTypeDetails() != null)
			{
				for (RoomTypeDetail rtd: newHotel.getHotelDetails().getRoomTypeDetails())
					rtd.setHotelName(
							RoomTypeDetail.createRoomTypeDetailHotelName(newHotel.getSource().getHotelName(),InventorySource.NODC));
			}
			
			HotelDetailCacheKey key = new HotelDetailCacheKey(newHotel.getSource().getHotelName(), InventorySource.NODC);
			HotelDetail existingHotelDetails = hotelDetailDAO.loadHotelDetailFromDB(key);
			HotelDetail newHotelDetails = newHotel.getHotelDetails();
			if (existingHotelDetails != null)
			{
				logger.error("hotel details previously stored");
				if (existingHotelDetails.getRoomTypeDetails().size() > 0)
				{
					logger.error("hotel room type details present.... deleting");
					roomTypeDetailDAO.delete(existingHotelDetails.getRoomTypeDetails());
				}
				keepNonOverridableContent(key, existingHotelDetails, newHotelDetails);
			}
			hotelDetailDAO.save(newHotelDetails);
		}
		
		cacheStateDAO.markHotelDetailCacheUpdated();
		cacheStateDAO.markRoomTypeCacheUpdated();
	}
	
	private void keepNonOverridableContent(HotelDetailCacheKey key, HotelDetail existing, HotelDetail updated)
	{
		Map<String, Boolean> overrideMap = hotelDetailDAO.loadHotelDetailOverridesAsMapFromDB(key);
		if (!FrenchQuarterGuideCacheLoader.isContentRefreshable(false, overrideMap, "address"))
			updated.setAddress1(existing.getAddress1());
		if (!FrenchQuarterGuideCacheLoader.isContentRefreshable(false, overrideMap, "city"))
			updated.setCity(existing.getCity());
		if (!FrenchQuarterGuideCacheLoader.isContentRefreshable(false, overrideMap, "state"))
			updated.setState(existing.getState());
		if (!FrenchQuarterGuideCacheLoader.isContentRefreshable(false, overrideMap, "zip"))
			updated.setZip(existing.getZip());
		if (!FrenchQuarterGuideCacheLoader.isContentRefreshable(false, overrideMap, "lat"))
			updated.setLatitude(existing.getLatitude());
		if (!FrenchQuarterGuideCacheLoader.isContentRefreshable(false, overrideMap, "long"))
			updated.setLongitude(existing.getLongitude());
		if (!FrenchQuarterGuideCacheLoader.isContentRefreshable(false, overrideMap, "amenities_json"))
			updated.setAmenitiesJsonString(existing.getAmenitiesJsonString());
		if (!FrenchQuarterGuideCacheLoader.isContentRefreshable(false, overrideMap, "photos_json"))
			updated.setPhotosJsonString(existing.getPhotosJsonString());
		if (!FrenchQuarterGuideCacheLoader.isContentRefreshable(false, overrideMap, "area_desc"))
			updated.setAreaDescription(existing.getAreaDescription());
		if (!FrenchQuarterGuideCacheLoader.isContentRefreshable(false, overrideMap, "desc"))
			updated.setDescription(existing.getDescription());
		if (!FrenchQuarterGuideCacheLoader.isContentRefreshable(false, overrideMap, "rating"))
			updated.setRating(existing.getRating());
	}
}