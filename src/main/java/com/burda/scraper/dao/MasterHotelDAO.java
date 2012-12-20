package com.burda.scraper.dao;

import com.burda.scraper.model.persisted.MasterHotel;

public class MasterHotelDAO extends AbstractDynamoDBDAO<MasterHotel>
{
	public MasterHotel getByHotelName(String name)
	{
			return getDynamoMapper().load(MasterHotel.class,  name);
	}
}
