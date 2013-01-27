package com.burda.scraper.inventory;

import javax.servlet.http.HttpServletRequest;

import com.burda.scraper.model.SearchParams;
import com.burda.scraper.model.SearchResult;
import com.burda.scraper.model.SortType;
import com.burda.scraper.model.persisted.HotelDetail;

public interface InventoryService
{
	void search(HttpServletRequest request, SearchParams params) throws Exception;
		
	HotelDetail getHotelDetails(String hotelName);

	SearchResult getAggragatedResults(SessionInfo sessionInfo, SortType sortBy, Integer page);
}
