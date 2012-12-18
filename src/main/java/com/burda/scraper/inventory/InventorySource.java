package com.burda.scraper.inventory;

import com.burda.scraper.model.SearchResult;

public interface InventorySource
{
	SearchResult getResults() throws Exception;

	
}
