package com.nodc.scraper.inventory;

import java.util.Collections;
import java.util.List;
import java.util.Map;

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
	public void deleteMasterRecord(String masterHotelId)
	{
		MasterHotel mh = masterHotelDAO.getByUuid(masterHotelId);
		if (mh != null)
			masterHotelDAO.delete(mh);
	}
	
	public void saveMasterRecord(MasterHotel submittedHotel, String newHotelName, int newWeight)
	{
		//find hotel detail (if any)
		   //find room type details (across inv_source)
		      //delete roomtypedetail
		      //add roomtypedetail (with new name)
		   //delete hotel detail
		   //add hotel detail with new name
		//find hotel detail override
		   //delete it
		   //add with new name
	  //find source hotel
		   //delete it
		   //add with new name
		//find 	master hotel
		   //delete it
		   //add with new name
		
		MasterHotel mh = null;
		String masterHotelId = submittedHotel.getUuid();
		if (submittedHotel != null && submittedHotel.getHotelName() != null && !submittedHotel.getHotelName().equals(newHotelName))
		{
			mh = masterHotelDAO.getByUuid(masterHotelId);
			if (mh == null)
			{
				logger.error("no mh found for id: " + masterHotelId);
				return;
			}
	
			String oldHotelName = mh.getHotelName();
			if (!oldHotelName.equals(newHotelName))
			{
				HotelDetailCacheKey fqgKey = new HotelDetailCacheKey(oldHotelName, InventorySource.FQG);
				HotelDetail hd = findHotelDetailWithOptionalNames(oldHotelName, InventorySource.FQG);
				List<RoomTypeDetail> nodcRoomTypeDetails = findRoomTypeDetailsWithOptionalNames(oldHotelName, InventorySource.NODC);
				List<RoomTypeDetail> fqgRoomTypeDetails = findRoomTypeDetailsWithOptionalNames(oldHotelName, InventorySource.FQG);
				if (nodcRoomTypeDetails != null)
				{
					for (RoomTypeDetail rtd: nodcRoomTypeDetails)
					{
						roomTypeDetailDAO.delete(rtd);
						rtd.setHotelName(RoomTypeDetail.createRoomTypeDetailHotelName(newHotelName, InventorySource.NODC));
						roomTypeDetailDAO.save(rtd);
					}
				}
	
				if (fqgRoomTypeDetails != null)
				{
					for (RoomTypeDetail rtd: fqgRoomTypeDetails)
					{
						roomTypeDetailDAO.delete(rtd);
						rtd.setHotelName(RoomTypeDetail.createRoomTypeDetailHotelName(newHotelName, InventorySource.FQG));
						roomTypeDetailDAO.save(rtd);
					}					
				}
				
				if (hd != null)
				{
					hotelDetailDAO.delete(hd);
					hd.setName(newHotelName);
					hotelDetailDAO.save(hd);				
				}
	
				Map<String, Boolean> overrideMap = hotelDetailDAO.loadHotelDetailOverridesAsMapFromDB(fqgKey);
				if (overrideMap != null && !overrideMap.isEmpty())
				{
					hotelDetailDAO.deleteHotelDetailOverrides(fqgKey);
					hotelDetailDAO.saveHotelDetailOverrides(fqgKey, overrideMap);				
				}
				
				SourceHotel fqgSource = findSourceHotelWithOptionalNames(oldHotelName, InventorySource.FQG);
				SourceHotel nodcSource = findSourceHotelWithOptionalNames(oldHotelName, InventorySource.NODC);
				if (fqgSource != null)
				{
					sourceHotelDAO.delete(fqgSource);
					fqgSource.setHotelName(newHotelName);
					sourceHotelDAO.save(fqgSource);				
				}
	
				if (nodcSource != null)
				{
					sourceHotelDAO.delete(nodcSource);
					nodcSource.setHotelName(newHotelName);
					sourceHotelDAO.save(nodcSource);				
				}
	
							
				masterHotelDAO.delete(mh);
				mh.setHotelName(newHotelName);
				masterHotelDAO.save(mh);
				logger.debug("updating hotel name from: " + oldHotelName + " to: " + newHotelName); 	
			}
		}
		
		if (!(submittedHotel.getWeight() == newWeight))
		{
			mh = masterHotelDAO.getByUuid(masterHotelId);
			logger.debug("updating weight of hotel: " + mh.getHotelName() + " from: " + mh.getWeight() + " to: " + newWeight);
			mh.setWeight(newWeight);
			masterHotelDAO.save(mh);
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
		return sourceHotelDAO.loadSourceHotelFromDB(sourceHotelId, is);
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
		MasterHotel mh = masterHotelDAO.loadMasterHotelFromDB(masterHotelName);
		SourceHotel sh = sourceHotelDAO.loadSourceHotelFromDB(sourceHotelId,  is);
		if (mh != null && sh != null)
		{
			HotelDetail oldHotelDetail = hotelDetailDAO
					.loadHotelDetailFromDB(new HotelDetailCacheKey(sh.getHotelName(), sh.getInvSource()));
			
			if (oldHotelDetail != null && oldHotelDetail.getRoomTypeDetails() != null)
			{
				for (RoomTypeDetail rtd: oldHotelDetail.getRoomTypeDetails())
				{
					roomTypeDetailDAO.delete(rtd);
					rtd.setHotelName(RoomTypeDetail.createRoomTypeDetailHotelName(masterHotelName,sh.getInvSource()));
					roomTypeDetailDAO.save(rtd);
				}				
			}			
			sourceHotelDAO.delete(sh);
			sh.setHotelName(masterHotelName);
			sourceHotelDAO.save(sh);			
		}
	}
	
	@Override
	public ContentEditor editHotelContent(String masterHotelId)
	{
		MasterHotel mh = masterHotelDAO.getByUuid(masterHotelId);
		
		ContentEditor ce = new ContentEditor("nodc_hotel_content", mh.getHotelName());
		
		HotelDetailCacheKey key = new HotelDetailCacheKey(mh.getHotelName(), InventorySource.FQG);
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
	
	private HotelDetail findHotelDetailWithOptionalNames(String hotelName, InventorySource is)
	{
		HotelDetailCacheKey ck = new HotelDetailCacheKey(hotelName, is);
		
		HotelDetail hd = hotelDetailDAO.loadHotelDetailFromDB(ck);
		REPLACE_STR replacementOption = REPLACE_STR.findAppropriate(hotelName);
		List<String> optionsToTry = replacementOption.replaceOptions();
		for (int i=0; i < optionsToTry.size() && hd==null; i++)
		{
			ck = new HotelDetailCacheKey(hotelName.replace(replacementOption.getValue(), optionsToTry.get(i)), is);
			hd = hotelDetailDAO.loadHotelDetailFromDB(ck);
		}
		return hd;
	}
	
	private List<RoomTypeDetail> findRoomTypeDetailsWithOptionalNames(String hotelName, InventorySource is)
	{
		List<RoomTypeDetail> rtds = Lists.newArrayList();
		HotelDetailCacheKey ck = new HotelDetailCacheKey(hotelName, is);
		
		rtds = roomTypeDetailDAO.loadRoomTypeDetailsFromDB(ck);
		REPLACE_STR replacementOption = REPLACE_STR.findAppropriate(hotelName);
		List<String> optionsToTry = replacementOption.replaceOptions();
		for (int i=0; i < optionsToTry.size() && (rtds==null || rtds.isEmpty()); i++)
		{
			ck = new HotelDetailCacheKey(hotelName.replace(replacementOption.getValue(), optionsToTry.get(i)), is);
			rtds = roomTypeDetailDAO.loadRoomTypeDetailsFromDB(ck);
		}
		return rtds;		
	}
	
	private SourceHotel findSourceHotelWithOptionalNames(String hotelName, InventorySource is)
	{
		SourceHotel sh = sourceHotelDAO.loadSourceHotelFromDB(hotelName, is);
		REPLACE_STR replacementOption = REPLACE_STR.findAppropriate(hotelName);
		List<String> optionsToTry = replacementOption.replaceOptions();
		for (int i=0; i < optionsToTry.size() && sh==null; i++)
		{
			sh = sourceHotelDAO.loadSourceHotelFromDBByHotelName(hotelName.replace(replacementOption.getValue(), optionsToTry.get(i)), is);
		}
		return sh;
	}
	
	private static enum REPLACE_STR 
	{
		TWO_ESCAPED_AMPERSANDS //&amp;amp;
		{
			List<String> replaceOptions()
			{
				return Lists.newArrayList("&", "&amp;");
			}
			String getValue() 
			{
				return "&amp;amp;";
			}
		},
		ONE_ESCAPED_AMPERSAND
		{
			List<String> replaceOptions()
			{
				return Lists.newArrayList("&", "&amp;amp;");
			}
			String getValue()
			{
				return "&amp;";
			}
		},
		ONE_AMPERSAND
		{
			List<String> replaceOptions()
			{
				return Lists.newArrayList("&amp;", "&amp;amp;");
			}
			String getValue()
			{
				return "&";
			}
		},
		NONE
		{
			List<String> replaceOptions()
			{
				return Lists.newArrayList();
			}
			String getValue()
			{
				return "";
			}
		};
		
		abstract List<String> replaceOptions();
		abstract String getValue();
		
		static REPLACE_STR findAppropriate(String hotelName)
		{
			//order is important when we evaluate options
			for (REPLACE_STR option: Lists.newArrayList(TWO_ESCAPED_AMPERSANDS, ONE_ESCAPED_AMPERSAND, ONE_AMPERSAND))
				if (hotelName.contains(option.getValue()))
					return option;
			
			return NONE;
		}
	}
}