package com.nodc.scraper.dao;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.amazonaws.services.dynamodb.AmazonDynamoDB;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodb.model.AttributeValue;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.nodc.scraper.cache.AbstractCacheRefresher;
import com.nodc.scraper.model.persisted.InventorySource;
import com.nodc.scraper.model.persisted.RoomTypeDetail;

@Repository("roomTypeDetailDAO")
public class RoomTypeDetailDAO extends AbstractDynamoDBDAO<RoomTypeDetail>
{
	private static Logger logger = LoggerFactory.getLogger(RoomTypeDetailDAO.class);
	private final static String CACHE_STATE_NAME = "room_type_detail";

	private final class RoomTypeDetailCacheRefresher extends AbstractCacheRefresher
	{
		private RoomTypeDetailCacheRefresher()
		{
			super(CACHE_STATE_NAME, cacheStateDAO);
		}
		
		@Override
		protected void loadCache()
		{
			List<RoomTypeDetail> allDetails = getDynamoMapper().scan(RoomTypeDetail.class,  new DynamoDBScanExpression());
			Multimap<String, RoomTypeDetail> roomTypesPerKey = HashMultimap.create();
			for (RoomTypeDetail rd: allDetails)
				roomTypesPerKey.put(rd.getHotelName(), rd);
			
			for (String keyString : roomTypesPerKey.keySet())
			{
				String[] keyParts = keyString.split("_");
				String hotelName = keyParts[0];
				InventorySource is = InventorySource.valueOf(keyParts[1]);
				HotelDetailCacheKey key = new HotelDetailCacheKey(hotelName, is);
				roomTypeDetailCache.put(key, Lists.newArrayList(roomTypesPerKey.get(keyString)));				
			}			
		}
	}	
	
	private LoadingCache<HotelDetailCacheKey, List<RoomTypeDetail>> roomTypeDetailCache = CacheBuilder.newBuilder()
      //.expireAfterWrite(1, TimeUnit.HOURS)
      .build(
          new CacheLoader<HotelDetailCacheKey, List<RoomTypeDetail>>() {
            public List<RoomTypeDetail> load(HotelDetailCacheKey key) {
              return loadRoomTypeDetailsFromDB(key);
            }
          });
	
	private final CacheStateDAO cacheStateDAO;
	
	@Autowired
	public RoomTypeDetailDAO(AmazonDynamoDB client, CacheStateDAO csDAO)
	{
		super(client);
		this.cacheStateDAO = csDAO;
		new RoomTypeDetailCacheRefresher().startAndWait();
	}
	
	//@ReadThroughSingleCache(namespace = "RoomTypeDetail", expiration = 3600)
	List<RoomTypeDetail> getRoomTypeDetails(/*@ParameterValueKeyProvider(order=1)*/ HotelDetailCacheKey ck)
	{
		List<RoomTypeDetail> details = null;
		try
		{
			details = roomTypeDetailCache.get(ck);	
		}
		catch (Exception ee)
		{
			logger.error("error loading rtds", ee);
		}
		return details;	
	}	
	
	private List<RoomTypeDetail> loadRoomTypeDetailsFromDB(HotelDetailCacheKey ck)
	{
		DynamoDBQueryExpression queryExpression = 
				new DynamoDBQueryExpression(new AttributeValue().withS(ck.getHotelName()+"_"+ck.getInventorySource().name()));

		List<RoomTypeDetail> roomTypeDetails = Lists.newArrayList();
		roomTypeDetails.addAll(getDynamoMapper().query(RoomTypeDetail.class, queryExpression));
		
		return roomTypeDetails;		
	}
}