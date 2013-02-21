package com.nodc.scraper.controller;

import java.util.List;

import com.nodc.scraper.model.persisted.MasterHotel;
import com.nodc.scraper.model.persisted.MasterHotel.EditableMasterHotel;
import com.google.common.collect.Lists;

public class SaveMasterHotelWrapper
{
	private List<EditableMasterHotel> masterHotels = Lists.newArrayList();
	
	public List<EditableMasterHotel> getMasterHotels()
	{
		return masterHotels;
	}
	
	public void setMasterHotels(List<EditableMasterHotel> hL)
	{
		this.masterHotels = hL;
	}
}
