package com.burda.scraper.inventory;


import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import com.burda.scraper.model.Hotel;
import com.burda.scraper.model.SearchParams;
import com.burda.scraper.model.SearchResult;

public interface Warehouse
{
	Collection<Hotel> getInitialResultsAndAsyncContinue(HttpServletRequest request, SearchParams params) throws Exception;

	
}
