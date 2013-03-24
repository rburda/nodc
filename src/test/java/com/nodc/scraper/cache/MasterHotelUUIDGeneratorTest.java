package com.nodc.scraper.cache;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.nodc.scraper.BaseSpringJUnitTest;

public class MasterHotelUUIDGeneratorTest extends BaseSpringJUnitTest
{
	@Autowired
	MasterHotelUUIDGenerator generator;
	
	@Test
	public void testGenerateUUIds()
	{
		generator.initializeUUIDs();
	}
	
}
