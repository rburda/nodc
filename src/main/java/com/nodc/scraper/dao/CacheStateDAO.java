package com.nodc.scraper.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.amazonaws.services.dynamodb.AmazonDynamoDB;
import com.nodc.scraper.model.persisted.CacheState;

@Repository("cacheStateDAO")
public class CacheStateDAO extends AbstractDynamoDBDAO<CacheState>
{
	@Autowired
	public CacheStateDAO(AmazonDynamoDB client)
	{
		super(client);
	}
	
	public CacheState getCacheState(String cacheName)
	{
		return getDynamoMapper().load(CacheState.class,  cacheName);
	}
	
	public void markHotelDetailCacheUpdated()
	{
		CacheState cs = getCacheState("nodc_hotel_content");
		if (cs != null)
		{
			cs.markCacheAsUpdated();
			save(cs);
		}
	}
	
	public void markRoomTypeCacheUpdated()
	{
		CacheState cs = getCacheState("nodc_hotel_room_type_content");
		if (cs != null)
		{
			cs.markCacheAsUpdated();
			save(cs);
		}
	}
}