package com.nodc.scraper.dao;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.amazonaws.services.dynamodb.AmazonDynamoDB;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBScanExpression;
import com.nodc.scraper.model.persisted.MasterHotel;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

@Repository("masterHotelDAO")
public class MasterHotelDAO extends AbstractDynamoDBDAO<MasterHotel>
{
	private Logger logger = LoggerFactory.getLogger(MasterHotelDAO.class);
	private LoadingCache<String, MasterHotel> masterCache = CacheBuilder.newBuilder()
      .expireAfterWrite(1, TimeUnit.HOURS)
      .build(
          new CacheLoader<String, MasterHotel>() {
            public MasterHotel load(String key) {
              return loadMasterHotel(key);
            }
          });
	
	@Autowired
	public MasterHotelDAO(AmazonDynamoDB client)
	{
		super(client);
	}
	
	//@ReadThroughSingleCache(namespace = "MasterHotel", expiration = 3600)
	public MasterHotel getByHotelName(/*@ParameterValueKeyProvider*/ String name)
	{
		MasterHotel mh = null;
		try
		{
			mh = masterCache.get(name);
		}
		catch (Exception ee)
		{
			logger.error("Unable to load master hotel", ee);
		}
		return mh;
	}
	
	
	
	public List<MasterHotel> getAll()
	{
		return getDynamoMapper().scan(MasterHotel.class,  new DynamoDBScanExpression());
	}
	
	public MasterHotel loadMasterHotel(String name)
	{
		return getDynamoMapper().load(MasterHotel.class,  name);
	}
	
	@Override
	public void save(List<MasterHotel> tList)
	{
		super.save(tList);
		for (MasterHotel mh: tList)
			masterCache.put(mh.getHotelName(), mh);
	}
}

