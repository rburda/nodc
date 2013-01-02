package com.burda.scraper.inventory;

import javax.servlet.http.HttpServletRequest;

import com.burda.scraper.model.SearchParams;
import com.burda.scraper.model.SearchResult;

public interface InventoryService
{
	SearchResult getSearchResult(HttpServletRequest request, SearchParams params) throws Exception;
	
	SearchResult getUpdatedResults(String sessionId);
}
