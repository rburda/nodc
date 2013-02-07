package com.burda.scraper.cache;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.burda.scraper.dao.HotelDetailCacheKey;
import com.burda.scraper.dao.HotelDetailDAO;
import com.burda.scraper.dao.MasterHotelDAO;
import com.burda.scraper.dao.RoomTypeDetailDAO;
import com.burda.scraper.dao.SourceHotelDAO;
import com.burda.scraper.inventory.NODCWarehouse;
import com.burda.scraper.model.Hotel;
import com.burda.scraper.model.persisted.HotelDetail;
import com.burda.scraper.model.persisted.InventorySource;
import com.burda.scraper.model.persisted.MasterHotel;
import com.burda.scraper.model.persisted.RoomTypeDetail;
import com.burda.scraper.model.persisted.SourceHotel;

public class NODCHotelLoader
{
	private static final Logger logger = LoggerFactory.getLogger(NODCHotelLoader.class);
	
	@Autowired
	@Qualifier("nodcInvSource")
	NODCWarehouse invSource;
	
	@Autowired
	@Qualifier("sourceHotelDAO")
	SourceHotelDAO sourceHotelDAO;
	
	@Autowired
	@Qualifier("masterHotelDAO")
	MasterHotelDAO masterHotelDAO;
	
	@Autowired
	@Qualifier("hotelDetailDAO")
	HotelDetailDAO hotelDetailDAO;
	
	@Autowired
	@Qualifier("roomTypeDetailDAO")
	RoomTypeDetailDAO roomTypeDetailDAO;
	
	public void loadCache() throws Exception
	{
		List<Hotel> shellHotels = invSource.getAllShellHotels();
		
		for (Hotel h: shellHotels)
		{
			logger.error(String.format("saving hotel: %1$s (%2$s)", h.getName(), h.getSource().getExternalHotelId()));
			String hotelId = h.getSource().getExternalHotelId();
			
			//check to see if we've seen this hotel before (on the NODC side)
			SourceHotel previouslyFound = 
					sourceHotelDAO.getByHotelId(hotelId, InventorySource.NODC);
			//if we've never seen this hotel before
			if (previouslyFound == null)
			{
				logger.error("hotel not previously found");
				sourceHotelDAO.save(h.getSource());				
				
				//now check to see if a masterHotel has been created with this name
				//previously
				MasterHotel masterHotel = masterHotelDAO.getByHotelName(h.getSource().getHotelName());
				if (masterHotel == null)
				{
					masterHotel = new MasterHotel();
					masterHotel.setHotelName(h.getSource().getHotelName());
					masterHotel.setWeight(1000);
				}
				masterHotel.setFavoredInventorySource(InventorySource.NODC);
				masterHotelDAO.save(masterHotel);
			}
			else
				logger.error("hotel previously found");
			
			HotelDetail hd = hotelDetailDAO.getHotelDetail(new HotelDetailCacheKey(h.getSource().getHotelName(), InventorySource.NODC));
			if (hd != null)
			{
				logger.error("hotel details previously stored");
				if (hd.getRoomTypeDetails().size() > 0)
				{
					logger.error("hotel room type details present.... deleting");
					roomTypeDetailDAO.delete(hd.getRoomTypeDetails());
				}
			}
			hotelDetailDAO.save(h.getHotelDetails());
		}
	}
}
