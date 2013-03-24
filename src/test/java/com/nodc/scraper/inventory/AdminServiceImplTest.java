package com.nodc.scraper.inventory;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.nodc.scraper.BaseSpringJUnitTest;
import com.nodc.scraper.model.persisted.MasterHotel;

public class AdminServiceImplTest extends BaseSpringJUnitTest
{
	@Autowired
	AdminServiceImpl adminService;
	
	@Test
	public void testUpdateMasterHotelName() throws Exception
	{
		MasterHotel mh = new MasterHotel();
		mh.setUuid("f680cebd-6f74-4b14-8303-21cff3915948");
		adminService.saveMasterRecord(mh, "La Quinta Inn and Suites Slidell", 283);
	}
}
