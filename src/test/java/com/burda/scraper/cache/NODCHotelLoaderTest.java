package com.burda.scraper.cache;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.burda.scraper.BaseSpringJUnitTest;

public class NODCHotelLoaderTest extends BaseSpringJUnitTest
{
	@Autowired
	private NODCHotelLoader hotelLoader;
	
	@Test
	public void testLoadCache() throws Exception
	{
		hotelLoader.loadCache();
	}
}
