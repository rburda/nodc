package com.burda.scraper.inventory;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.burda.scraper.model.SearchParams;
import com.burda.scraper.model.SearchResult;
import com.burda.scraper.model.SortType;
import com.burda.scraper.model.persisted.HotelDetail;
import com.burda.scraper.model.persisted.InventorySource;
import com.burda.scraper.model.persisted.MasterHotel;
import com.burda.scraper.model.persisted.SourceHotel;

public interface InventoryService
{
	void search(HttpServletRequest request, SearchParams params) throws Exception;
		
	HotelDetail getHotelDetails(SessionInfo sessionInfo, String hotelName);

	SearchResult getAggragatedResults(SessionInfo sessionInfo, SortType sortBy, Integer page);
	
	List<MasterHotel> getMasterRecords();
	
	void saveMasterRecords(List<MasterHotel> hotels);
	
	void deleteMasterRecord(String masterHotelName);
	
	List<SourceHotel> getSourceHotels();
	
	SourceHotel getSourceHotel(String sourceHotelId, InventorySource is);
	
	void updateSourceHotelName(String sourceHotelId, InventorySource is, String masterHotelName);
}
