package com.burda.scraper.inventory;

import java.util.Collection;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.burda.scraper.BaseSpringJUnitTest;
import com.burda.scraper.model.Hotel;
import com.burda.scraper.model.SearchParams;
import com.burda.scraper.model.SearchResult;

public class FrenchQuarterGuideInventorySourceTest extends BaseSpringJUnitTest
{
	@Autowired
	private FrenchQuarterGuideInventorySource invSource;
	
	@Test
	public void testGetSearchResult() throws Exception
	{
		Collection<Hotel> hotels = invSource.getInitialResultsAndAsyncContinue(null, SearchParams.oneRoomOneAdult());
		int x = 1;
		
	}
}
