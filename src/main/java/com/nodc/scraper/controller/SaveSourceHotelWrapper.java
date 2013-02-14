package com.nodc.scraper.controller;

import java.util.List;

import com.nodc.scraper.model.persisted.SourceHotel;

public class SaveSourceHotelWrapper
{
	private List<SourceHotel> sourceHotels;

	public List<SourceHotel> getSourceHotels()
	{
		return sourceHotels;
	}

	public void setSourceHotels(List<SourceHotel> sourceHotels)
	{
		this.sourceHotels = sourceHotels;
	}
}
