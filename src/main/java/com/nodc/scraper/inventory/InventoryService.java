package com.nodc.scraper.inventory;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.nodc.scraper.model.SearchParams;
import com.nodc.scraper.model.SearchResult;
import com.nodc.scraper.model.SortType;
import com.nodc.scraper.model.persisted.HotelDetail;
import com.nodc.scraper.model.persisted.InventorySource;
import com.nodc.scraper.model.persisted.MasterHotel;
import com.nodc.scraper.model.persisted.MasterHotel.EditableMasterHotel;
import com.nodc.scraper.model.persisted.SourceHotel;

public interface InventoryService
{
	void search(HttpServletRequest request, SearchParams params) throws Exception;
		
	HotelDetail getHotelDetails(SessionInfo sessionInfo, String hotelName);

	SearchResult getAggragatedResults(SessionInfo sessionInfo, SortType sortBy, Integer page);
}
