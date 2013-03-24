package com.nodc.scraper.inventory;

import java.util.List;

import com.nodc.scraper.model.persisted.InventorySource;
import com.nodc.scraper.model.persisted.MasterHotel;
import com.nodc.scraper.model.persisted.MasterHotel.EditableMasterHotel;
import com.nodc.scraper.model.persisted.SourceHotel;

public interface AdminService
{
	List<MasterHotel> getMasterRecords();
	
	void saveMasterRecords(List<EditableMasterHotel> masterHotels);
	
	void deleteMasterRecord(String masterHotelName);
	
	List<SourceHotel> getSourceHotels();
	
	SourceHotel getSourceHotel(String sourceHotelId, InventorySource is);
	
	void updateSourceHotelName(String sourceHotelId, InventorySource is, String masterHotelName);
	
	ContentEditor editHotelContent(String masterHotelId);
	
	void saveHotelContent(ContentEditor ce);
	
	ContentEditor editRoomTypeDetail(String hotelName, InventorySource is);
	
	void saveRoomTypeDetail(ContentEditor ce);
}