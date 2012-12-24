package com.burda.scraper.dao;

import com.burda.scraper.model.persisted.MasterHotel;
import com.google.code.ssm.api.ParameterValueKeyProvider;
import com.google.code.ssm.api.ReadThroughSingleCache;

public class MasterHotelDAO extends AbstractDynamoDBDAO<MasterHotel>
{
	//@ReadThroughSingleCache(namespace = "MasterHotel", expiration = 3600)
	public MasterHotel getByHotelName(/*@ParameterValueKeyProvider*/ String name)
	{
			return getDynamoMapper().load(MasterHotel.class,  name);
	}
}
