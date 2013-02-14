package com.nodc.scraper.inventory;

import java.util.Collection;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.nodc.scraper.BaseSpringJUnitTest;
import com.nodc.scraper.model.Hotel;
import com.nodc.scraper.model.SearchParams;
import com.nodc.scraper.model.SearchResult;

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
