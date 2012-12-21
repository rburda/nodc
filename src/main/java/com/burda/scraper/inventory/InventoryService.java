package com.burda.scraper.inventory;

import com.burda.scraper.model.SearchParams;
import com.burda.scraper.model.SearchResult;

public interface InventoryService
{
	public SearchResult getSearchResult(SearchParams params) throws Exception;
}
