package com.nodc.scraper.inventory;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.services.dynamodb.AmazonDynamoDB;
import com.google.common.collect.Lists;
import com.nodc.scraper.dao.CacheStateDAO;
import com.nodc.scraper.dao.HotelDetailCacheKey;
import com.nodc.scraper.dao.HotelDetailDAO;
import com.nodc.scraper.dao.MasterHotelDAO;
import com.nodc.scraper.dao.RoomTypeDetailDAO;
import com.nodc.scraper.dao.SourceHotelDAO;
import com.nodc.scraper.model.persisted.HotelDetail;
import com.nodc.scraper.model.persisted.InventorySource;
import com.nodc.scraper.model.persisted.MasterHotel;
import com.nodc.scraper.model.persisted.MasterHotel.EditableMasterHotel;
import com.nodc.scraper.model.persisted.RoomTypeDetail;
import com.nodc.scraper.model.persisted.SourceHotel;

@Service("adminService")
public class AdminServiceImpl implements AdminService
{
	private static final Logger logger = LoggerFactory.getLogger(AdminServiceImpl.class);
  private final SourceHotelDAO sourceHotelDAO;
  private final MasterHotelDAO masterHotelDAO;
  private final RoomTypeDetailDAO roomTypeDetailDAO;
  private final HotelDetailDAO hotelDetailDAO;
  private final CacheStateDAO cacheStateDAO;
  private final AmazonDynamoDB client;
	
  @Autowired
  public AdminServiceImpl(
  		AmazonDynamoDB client, SourceHotelDAO shDAO, MasterHotelDAO mhDAO, 
  		HotelDetailDAO hdDAO, RoomTypeDetailDAO rtdDAO, CacheStateDAO csDAO)
  {
  	this.client = client;
  	this.sourceHotelDAO = shDAO;
  	this.masterHotelDAO = mhDAO;
  	this.hotelDetailDAO = hdDAO;
  	this.roomTypeDetailDAO = rtdDAO;
  	this.cacheStateDAO = csDAO;
  }
  
	@Override
	public List<MasterHotel> getMasterRecords()
	{
		List<MasterHotel> hotels = Lists.newArrayList(masterHotelDAO.getAll());
		Collections.sort(hotels, MasterHotel.BY_WEIGHT);
		return hotels;
	}
	
	@Override 
	public void deleteMasterRecord(String masterHotelName)
	{
		MasterHotel mh = masterHotelDAO.loadMasterHotel(masterHotelName);
		if (mh != null)
			masterHotelDAO.delete(mh);
	}
	
	@Override
	public void saveMasterRecord(MasterHotel hotel, String newHotelName, int newWeight)
	{
		if (!hotel.getHotelName().equals(newHotelName))
		{
			masterHotelDAO.delete(hotel);
			hotel.setHotelName(newHotelName);
			masterHotelDAO.save(hotel);
			logger.debug("updating hotel name from: " + hotel.getHotelName() + " to: " + newHotelName); 	
		}
		if (!(hotel.getWeight() == newWeight))
		{
			logger.debug("updating weight of hotel: " + hotel.getHotelName() + " from: " + hotel.getWeight() + " to: " + newWeight);
			hotel.setWeight(newWeight);
			masterHotelDAO.save(hotel);
		}

	}
	
	@Override
	public void saveMasterRecords(List<EditableMasterHotel> masterHotels)
	{
		for (EditableMasterHotel emh: masterHotels)
			saveMasterRecord(emh, emh.getNewHotelName(), emh.getNewWeight());
	}
	
	@Override 
	public SourceHotel getSourceHotel(String sourceHotelId, InventorySource is)
	{
		return sourceHotelDAO.getByHotelId(sourceHotelId, is);
	}
	
	@Override
	public List<SourceHotel> getSourceHotels()
	{
		List<SourceHotel> sourceHotels = Lists.newArrayList(sourceHotelDAO.getAll());
		Collections.sort(sourceHotels, SourceHotel.BY_NAME);
		
		return sourceHotels;
	}
	
	@Override 
	public void updateSourceHotelName(String sourceHotelId, InventorySource is, String masterHotelName)
	{
		MasterHotel mh = masterHotelDAO.loadMasterHotel(masterHotelName);
		SourceHotel sh = sourceHotelDAO.getByHotelId(sourceHotelId,  is);
		if (mh != null && sh != null)
		{
			HotelDetail oldHotelDetail = hotelDetailDAO
					.loadHotelDetailFromDB(new HotelDetailCacheKey(sh.getHotelName(), sh.getInvSource()));
			
			if (oldHotelDetail != null && oldHotelDetail.getRoomTypeDetails() != null)
			{
				for (RoomTypeDetail rtd: oldHotelDetail.getRoomTypeDetails())
				{
					roomTypeDetailDAO.delete(rtd);
					rtd.setHotelName(masterHotelName+"_"+sh.getInvSource().name());
					roomTypeDetailDAO.save(rtd);
				}				
			}			
			sourceHotelDAO.delete(sh);
			sh.setHotelName(masterHotelName);
			sourceHotelDAO.save(sh);			
		}
	}
	
	@Override
	public ContentEditor editHotelContent(String masterHotelName)
	{
		ContentEditor ce = new ContentEditor("nodc_hotel_content", masterHotelName);
		
		HotelDetailCacheKey key = new HotelDetailCacheKey(masterHotelName, InventorySource.FQG);
		ce.addAttributes(hotelDetailDAO.loadHotelDetailAsMapFromDB(key));
		ce.addOverrideStatus(hotelDetailDAO.loadHotelDetailOverridesAsMapFromDB(key));
		return ce;
	}
	
	@Override
	public void saveHotelContent(ContentEditor ce)
	{
		ce.save(client, cacheStateDAO);
	}
	
	public ContentEditor editRoomTypeDetail(String hotelName, InventorySource is)
	{
		String key = hotelName+"_"+is.name();
		ContentEditor ce = new ContentEditor("nodc_hotel_room_type_content", key);
		
		return ce;
	}
	
	public void saveRoomTypeDetail(ContentEditor ce)
	{
		ce.save(client, cacheStateDAO);
	}
}