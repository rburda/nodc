package com.rburda.scraper.cache;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.burda.scraper.cache.OneTimeHotelLoader;
import com.rburda.scraper.BaseSpringJUnitTest;

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
