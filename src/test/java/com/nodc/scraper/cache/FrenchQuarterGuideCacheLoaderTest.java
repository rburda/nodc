package com.nodc.scraper.cache;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.nodc.scraper.BaseSpringJUnitTest;


public class FrenchQuarterGuideCacheLoaderTest extends BaseSpringJUnitTest
{
	
	@Autowired
	FrenchQuarterGuideCacheLoader cacheLoader;
	
	@Test
	public void testLoadCache() throws Exception
	{
		cacheLoader.loadCache("Queen and Crescent Hotel");
	}
}
