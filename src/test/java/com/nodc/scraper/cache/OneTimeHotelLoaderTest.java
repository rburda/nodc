package com.nodc.scraper.cache;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.nodc.scraper.BaseSpringJUnitTest;

public class OneTimeHotelLoaderTest extends BaseSpringJUnitTest
{
	@Autowired
	OneTimeHotelLoader hotelLoader;
	
	@Test
	public void testOneTimeHotelLoad() throws Exception
	{
		hotelLoader.initializeHotelData();
	}
}
