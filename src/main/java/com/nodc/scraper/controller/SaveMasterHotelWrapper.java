package com.nodc.scraper.controller;

import java.util.List;

import com.nodc.scraper.model.persisted.MasterHotel;
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
