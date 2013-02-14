package com.nodc.scraper.inventory;


import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import com.nodc.scraper.model.Hotel;
import com.nodc.scraper.model.SearchParams;
import com.nodc.scraper.model.SearchResult;

public interface Warehouse
{
	Collection<Hotel> getInitialResultsAndAsyncContinue(HttpServletRequest request, SearchParams params) throws Exception;

	
}
