package com.nodc.scraper.cache;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.nodc.scraper.dao.MasterHotelDAO;
import com.nodc.scraper.model.persisted.MasterHotel;

@Component
public class MasterHotelUUIDGenerator
{
	private final MasterHotelDAO masterHotelDAO;
	
	@Autowired
	public MasterHotelUUIDGenerator(MasterHotelDAO mhDAO)
	{
		this.masterHotelDAO = mhDAO;
	}
	
	public void initializeUUIDs()
	{
		for (MasterHotel mh: masterHotelDAO.getAll())
		{
			if (mh.getUuid() == null)
			{
				mh.setUuid(UUID.randomUUID().toString());
				masterHotelDAO.save(mh);
			}
		}
	}
}