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
}
