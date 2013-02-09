package com.burda.scraper.controller;

import java.util.List;

import com.burda.scraper.model.persisted.MasterHotel;
import com.google.common.collect.Lists;

public class SaveMasterHotelWrapper
{
	private List<MasterHotel> masterHotels = Lists.newArrayList();
	
	public List<MasterHotel> getMasterHotels()
	{
		return masterHotels;
	}
	
	public void setMasterHotels(List<MasterHotel> hL)
	{
		this.masterHotels = hL;
	}
}
