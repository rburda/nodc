package com.rburda.scraper.cache;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.burda.scraper.cache.FrenchQuarterGuideCacheLoader;
import com.rburda.scraper.BaseSpringJUnitTest;


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
