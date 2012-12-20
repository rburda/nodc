package com.burda.scraper.dao;

import com.burda.scraper.model.persisted.HotelDetail;

public class HotelDetailDAO extends AbstractDynamoDBDAO<HotelDetail>
{
	public HotelDetail getHotelDetail(String hotelName)
	{
		return getDynamoMapper().load(HotelDetail.class,  hotelName);
	}
	
}
