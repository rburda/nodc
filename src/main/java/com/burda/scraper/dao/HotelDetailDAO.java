package com.burda.scraper.dao;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.burda.scraper.model.persisted.HotelDetail;
import com.google.code.ssm.api.ParameterValueKeyProvider;
import com.google.code.ssm.api.ReadThroughSingleCache;

public class HotelDetailDAO extends AbstractDynamoDBDAO<HotelDetail>
{
	private Logger logger = LoggerFactory.getLogger(HotelDetailDAO.class);
	
	@ReadThroughSingleCache(namespace = "HotelDetail", expiration = 3600)
	public HotelDetail getHotelDetail(@ParameterValueKeyProvider(order=1) HotelDetailCacheKey ck)
	{
		return getDynamoMapper().load(HotelDetail.class,  ck.getHotelName());
	}
}
