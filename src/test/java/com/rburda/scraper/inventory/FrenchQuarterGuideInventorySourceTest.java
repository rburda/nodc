package com.rburda.scraper.inventory;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.burda.scraper.inventory.FrenchQuarterGuideInventorySource;
import com.burda.scraper.model.SearchParams;
import com.burda.scraper.model.SearchResult;
import com.rburda.scraper.BaseSpringJUnitTest;

public class FrenchQuarterGuideInventorySourceTest extends BaseSpringJUnitTest
{
	@Autowired
	private FrenchQuarterGuideInventorySource invSource;
	
	@Test
	public void testGetSearchResult() throws Exception
	{
		SearchResult sr = invSource.getResults(SearchParams.oneRoomOneAdult());
		int x = 1;
		
	}
}
