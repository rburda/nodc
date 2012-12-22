package com.burda.scraper.cache;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.burda.scraper.BaseSpringJUnitTest;

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
