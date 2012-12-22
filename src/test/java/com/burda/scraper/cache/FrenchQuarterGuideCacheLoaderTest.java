package com.burda.scraper.cache;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.burda.scraper.BaseSpringJUnitTest;


public class FrenchQuarterGuideCacheLoaderTest extends BaseSpringJUnitTest
{
	
	@Autowired
	FrenchQuarterGuideCacheLoader cacheLoader;
	
	@Test
	public void testLoadCache() throws Exception
	{
		cacheLoader.loadCache();
	}
}
