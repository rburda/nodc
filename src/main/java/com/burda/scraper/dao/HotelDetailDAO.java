package com.burda.scraper.dao;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.burda.scraper.model.persisted.HotelDetail;
import com.burda.scraper.model.persisted.RoomTypeDetail;
import com.google.code.ssm.api.ParameterValueKeyProvider;
import com.google.code.ssm.api.ReadThroughSingleCache;

public class HotelDetailDAO extends AbstractDynamoDBDAO<HotelDetail>
{
	private Logger logger = LoggerFactory.getLogger(HotelDetailDAO.class);
	private RoomTypeDetailDAO roomTypeDetailDAO;
	
	@ReadThroughSingleCache(namespace = "HotelDetail", expiration = 3600)
	public HotelDetail getHotelDetail(@ParameterValueKeyProvider(order=1) HotelDetailCacheKey ck)
	{
		HotelDetail hd = getDynamoMapper().load(HotelDetail.class,  ck.getHotelName());
		if (hd != null)
		{
			List<RoomTypeDetail> roomTypes = roomTypeDetailDAO.getRoomTypeDetails(ck);
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
	
	public void setRoomTypeDetailDAO(RoomTypeDetailDAO rtdDAO)
	{
		this.roomTypeDetailDAO = rtdDAO;
	}
}