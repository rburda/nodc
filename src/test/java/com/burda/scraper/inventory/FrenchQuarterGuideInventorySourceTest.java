package com.burda.scraper.inventory;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.burda.scraper.BaseSpringJUnitTest;
import com.burda.scraper.model.SearchParams;
import com.burda.scraper.model.SearchResult;

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
