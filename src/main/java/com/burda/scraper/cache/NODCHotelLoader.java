package com.burda.scraper.cache;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;

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
	NODCWarehouse invSource;
	
	@Autowired
	SourceHotelDAO sourceHotelDAO;
	
	@Autowired
	MasterHotelDAO masterHotelDAO;
	
	@Autowired
	HotelDetailDAO hotelDetailDAO;
	
	@Autowired
	RoomTypeDetailDAO roomTypeDetailDAO;
	
	public void updateWeights(Map<String, Integer> weights)
	{
		for (String hotelName: weights.keySet())
		{
			MasterHotel h = masterHotelDAO.getByHotelName(hotelName);
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
				if (h.getSource().getHotelName().contains("Harrah"))
				{
					h.getSource().setHotelName(h.getSource().getHotelName().replace("'",""));
				}
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
			{
				logger.error("hotel previously found");
				h.setSource(previouslyFound);
			}
			h.setName(h.getSource().getHotelName());
			h.getHotelDetails().setName(h.getSource().getHotelName());
			if (h.getHotelDetails().getRoomTypeDetails() != null)
			{
				for (RoomTypeDetail rtd: h.getHotelDetails().getRoomTypeDetails())
					rtd.setHotelName(h.getSource().getHotelName()+"_"+InventorySource.NODC);
			}
			
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
	
	public void setSourceHotelDAO(SourceHotelDAO dao)
	{
		this.sourceHotelDAO = dao;
	}
	public void setMasterHotelDAO(MasterHotelDAO dao)
	{
		this.masterHotelDAO = dao;
	}
	public void setRoomTypeDetailDAO(RoomTypeDetailDAO dao)
	{
		this.roomTypeDetailDAO = dao;
	}
	public void setHotelDetailDAO(HotelDetailDAO dao)
	{
		this.hotelDetailDAO = dao;
	}
	public void setNODCWarehouse(NODCWarehouse whs)
	{
		this.invSource = whs;
	}
}
