package com.nodc.scraper.dao;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.dynamodb.datamodeling.DynamoDBScanExpression;
import com.nodc.scraper.model.persisted.HotelDetail;
import com.nodc.scraper.model.persisted.InventorySource;
import com.nodc.scraper.model.persisted.MasterHotel;
import com.nodc.scraper.model.persisted.SourceHotel;
import com.google.code.ssm.api.ParameterValueKeyProvider;
import com.google.code.ssm.api.ReadThroughSingleCache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;

public class SourceHotelDAO extends AbstractDynamoDBDAO<SourceHotel>
{	
	private static final Logger logger = LoggerFactory.getLogger(SourceHotelDAO.class);
	private static class CacheKey 
	{
		private final InventorySource is;
		private final String hotelId;
		
		private CacheKey(String hotelId, InventorySource is)
		{
			this.is = is;
			this.hotelId = hotelId;
		}
		
		public boolean equals(Object o)
		{
			if (o == null)
				return false;
			
			if (!(o instanceof CacheKey))
				return false;
			
			CacheKey other = (CacheKey)o;
			if (is.equals(other.is))
				if (hotelId.equals(other.hotelId))
					return true;
			return false;
		}
	}
	
	private LoadingCache<CacheKey, SourceHotel> sourceCache = CacheBuilder.newBuilder()
      .expireAfterWrite(1, TimeUnit.HOURS)
      .build(
          new CacheLoader<CacheKey, SourceHotel>() {
            public SourceHotel load(CacheKey key) {
              return loadSourceHotel(key);
            }
            
            @Override
            public Map<CacheKey, SourceHotel> loadAll(Iterable<? extends CacheKey> keys)
            {
            	Map<CacheKey, SourceHotel> resultsMap = Maps.newHashMap();
            	for (SourceHotel sh: getAll())
            	{
            		resultsMap.put(new CacheKey(sh.getExternalHotelId(), sh.getInvSource()), sh);
            	}
            	
            	return resultsMap;
            }
          });
	
	//@ReadThroughSingleCache(namespace = "SourceHotel", expiration = 3600)
	public SourceHotel getByHotelId(
		/*@ParameterValueKeyProvider(order=1)*/ String hotelId, /*@ParameterValueKeyProvider(order=2)*/ InventorySource is)
	{
		SourceHotel s = null;
		try
		{
			s = sourceCache.get(new CacheKey(hotelId, is));
		}
		catch (Exception ee)
		{
			logger.error("unable to load source hotel", ee);
		}
		return s;
	}
	
	public List<SourceHotel> getAll()
	{
		return getDynamoMapper().scan(SourceHotel.class,  new DynamoDBScanExpression());
	}
	
	@Override
	public void save(List<SourceHotel> tList)
	{
		super.save(tList);
		for (SourceHotel sh: tList)
			sourceCache.put(new CacheKey(sh.getExternalHotelId(), sh.getInvSource()), sh);
	}
	
	private SourceHotel loadSourceHotel(CacheKey key)
	{
		return getDynamoMapper().load(SourceHotel.class,  key.hotelId, key.is.name());
		/*
		DynamoDBQueryExpression queryExpression = 
				new DynamoDBQueryExpression(
						new AttributeValue().withS(hotelId)).withRangeKeyCondition(
								new Condition().withAttributeValueList(
										new AttributeValue().withS(is.name())));
		*/
	}
}