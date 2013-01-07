package com.burda.scraper.inventory;

import javax.servlet.http.HttpServletRequest;

import com.burda.scraper.model.SearchParams;
import com.burda.scraper.model.SearchResult;
import com.burda.scraper.model.SortType;

public interface InventoryService
{
	void search(HttpServletRequest request, SearchParams params) throws Exception;
	
	SearchResult getUpdatedResults(String sessionId, SortType sortBy, Integer page);
}
