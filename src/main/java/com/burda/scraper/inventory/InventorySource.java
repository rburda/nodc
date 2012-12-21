package com.burda.scraper.inventory;

import com.burda.scraper.model.SearchParams;
import com.burda.scraper.model.SearchResult;

public interface InventorySource
{
	SearchResult getResults(SearchParams params) throws Exception;

	
}
