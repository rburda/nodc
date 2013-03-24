package com.nodc.scraper.dao;


import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.nodc.scraper.cache.AbstractCacheRefresher;
import com.nodc.scraper.model.persisted.CacheState;
import com.nodc.scraper.model.persisted.HotelDetail;
import com.nodc.scraper.model.persisted.MasterHotel;
import com.nodc.scraper.model.persisted.RoomTypeDetail;
import com.nodc.scraper.model.persisted.SourceHotel;
import com.amazonaws.services.dynamodb.AmazonDynamoDB;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodb.model.AttributeValue;
import com.amazonaws.services.dynamodb.model.DeleteItemRequest;
import com.amazonaws.services.dynamodb.model.GetItemRequest;
import com.amazonaws.services.dynamodb.model.GetItemResult;
import com.amazonaws.services.dynamodb.model.Key;
import com.amazonaws.services.dynamodb.model.PutItemRequest;
import com.google.code.ssm.api.ParameterValueKeyProvider;
import com.google.code.ssm.api.ReadThroughSingleCache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.AbstractScheduledService;

@Repository("hotelDetailDAO")
public class HotelDetailDAO extends AbstractDynamoDBDAO<HotelDetail>
{
	private final static String CACHE_STATE_NAME = "nodc_hotel_content";

	private final class HotelDetailCacheRefresher extends AbstractCacheRefresher
	{
		private HotelDetailCacheRefresher()
		{
			super(CACHE_STATE_NAME, cacheStateDAO);
		}
		
		@Override
		protected void loadCache()
		{
			List<SourceHotel> allSourceHotels = sourceHotelDAO.getAll();
			Map<HotelDetailCacheKey, HotelDetail> hotelDetailMap = Maps.newHashMap();
			for (SourceHotel sh: allSourceHotels)
			{
				HotelDetailCacheKey key = new HotelDetailCacheKey(sh.getHotelName(), sh.getInvSource());
				HotelDetail hd = loadHotelDetailFromDB(key);
				if (hd == null)
				{
					logger.error("unable to find hotel detail for key: " + key.getHotelName() + ", " + key.getInventorySource());
					
				}
				else
				{
					hotelDetailMap.put(key, loadHotelDetailFromDB(key));
				}
			}
			hotelDetailCache.putAll(hotelDetailMap);			
		}
	}
	
	private final Logger logger = LoggerFactory.getLogger(HotelDetailDAO.class);
	private final CacheStateDAO cacheStateDAO;
	private final SourceHotelDAO sourceHotelDAO;
	private final RoomTypeDetailDAO roomTypeDetailDAO;
	
	private final LoadingCache<HotelDetailCacheKey, HotelDetail> hotelDetailCache = CacheBuilder.newBuilder()
      //.expireAfterWrite(1, TimeUnit.HOURS)
      .build(
          new CacheLoader<HotelDetailCacheKey, HotelDetail>() {
            public HotelDetail load(HotelDetailCacheKey key) {
              return loadHotelDetailFromDB(key);
            }
          });
	
	@Autowired
	public HotelDetailDAO(AmazonDynamoDB dbClient, SourceHotelDAO shDAO, RoomTypeDetailDAO rtDAO, CacheStateDAO csDAO)
	{
		super(dbClient);
		this.sourceHotelDAO = shDAO;
		this.roomTypeDetailDAO = rtDAO;
		this.cacheStateDAO = csDAO;
		new HotelDetailCacheRefresher().startAndWait();
		logger.error("testing" + toString());
	}
	
	//@ReadThroughSingleCache(namespace = "HotelDetail", expiration = 3600)
	public HotelDetail getHotelDetail(/*@ParameterValueKeyProvider(order=1)*/ HotelDetailCacheKey ck)
	{
		HotelDetail hd = null;
		try
		{
			hd = hotelDetailCache.get(ck);
		}
		catch (Exception ee)
		{
			logger.error("Unable to load hotelDetail", ee);
		}
		return hd;
	}
	
	public Map<String, AttributeValue> loadHotelDetailAsMapFromDB(HotelDetailCacheKey ck)
	{
		GetItemRequest getItemRequest = new GetItemRequest()
	  .withTableName("nodc_hotel_content")
	  .withKey(new Key()
	  .withHashKeyElement(new AttributeValue().withS(ck.getHotelName())));

		GetItemResult result = getClient().getItem(getItemRequest);
		if (result != null)
			return result.getItem();
		else
			return null;
	}
	
	public void deleteHotelDetailOverrides(HotelDetailCacheKey ck)
	{
		DeleteItemRequest deleteItemRequest = new DeleteItemRequest()
			.withTableName("nodc_hotel_content_override")
			.withKey(new Key()
					.withHashKeyElement(new AttributeValue().withS(ck.getHotelName())));
		getClient().deleteItem(deleteItemRequest);
	}
	
	public Map<String, Boolean> loadHotelDetailOverridesAsMapFromDB(HotelDetailCacheKey ck)
	{
		Map<String, Boolean> overrideMap = Maps.newHashMap();
		
		GetItemRequest getItemRequest = new GetItemRequest()
	  .withTableName("nodc_hotel_content_override")
	  .withKey(new Key()
	  .withHashKeyElement(new AttributeValue().withS(ck.getHotelName())));
		

		GetItemResult result = getClient().getItem(getItemRequest);
		if (result != null && result.getItem() != null)
		{
			for (String key: result.getItem().keySet())
			{
				overrideMap.put(key,  Boolean.valueOf(result.getItem().get(key).getS()));
			}
		}
		return overrideMap;
	}
	
	public void saveHotelDetailOverrides(HotelDetailCacheKey ck, Map<String, Boolean> overrides)
	{
		Map<String, AttributeValue> persistOverrideMap = Maps.newHashMap();
		for (String key: overrides.keySet())
		{
			persistOverrideMap.put(key, new AttributeValue(overrides.get(key).toString()));
		}
		getClient().putItem( 
				new PutItemRequest().withTableName("nodc_hotel_content_override").withItem(persistOverrideMap));
	}
	
	public void saveHotelDetailFromMap(Map<String, AttributeValue> attrs)
	{
		getClient().putItem( 
				new PutItemRequest()
					.withTableName("nodc_hotel_content")
					.withItem(attrs));
	}
	
	public HotelDetail loadHotelDetailFromDB(HotelDetailCacheKey ck)
	{
		HotelDetail hd = getDynamoMapper().load(HotelDetail.class,  ck.getHotelName());
		if (hd != null)
		{
			List<RoomTypeDetail> roomTypes = roomTypeDetailDAO.loadRoomTypeDetailsFromDB(ck);
			if (roomTypes != null)
			{
				for (RoomTypeDetail rtd: roomTypes)
					hd.addRoomTypeDetail(rtd);
			}
		}
		return hd;		
	}
	
	@Override
	public void save(HotelDetail hd)
	{
		super.save(hd);
		if (hd.getRoomTypeDetails() != null && !hd.getRoomTypeDetails().isEmpty())
			roomTypeDetailDAO.save(hd.getRoomTypeDetails());
	}
}