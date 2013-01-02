package com.burda.scraper.cache;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.burda.scraper.dao.HotelDetailCacheKey;
import com.burda.scraper.dao.HotelDetailDAO;
import com.burda.scraper.dao.MasterHotelDAO;
import com.burda.scraper.dao.SourceHotelDAO;
import com.burda.scraper.inventory.NODCWarehouse;
import com.burda.scraper.model.Hotel;
import com.burda.scraper.model.persisted.HotelDetail;
import com.burda.scraper.model.persisted.InventorySource;
import com.burda.scraper.model.persisted.MasterHotel;
import com.burda.scraper.model.persisted.SourceHotel;

public class NODCHotelLoader
{
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
	
	public void loadCache() throws Exception
	{
		List<Hotel> shellHotels = invSource.getAllShellHotels();
		
		for (Hotel h: shellHotels)
		{
			String hotelId = h.getSource().getExternalHotelId();
			
			//check to see if we've seen this hotel before (on the NODC side)
			SourceHotel previouslyFound = 
					sourceHotelDAO.getByHotelId(hotelId, InventorySource.NODC);
			//if we've never seen this hotel before
			if (previouslyFound == null)
			{
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
			
			HotelDetail hd = hotelDetailDAO.getHotelDetail(new HotelDetailCacheKey(h.getSource().getHotelName()));
			if (hd == null)
			{
				hd = new HotelDetail();
				hd.setName(h.getSource().getHotelName());
				hotelDetailDAO.save(hd);
			}
		}
	}
}
