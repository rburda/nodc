package com.burda.scraper.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonIgnore;


public class SearchResult
{
	public Date startDate;
	public Date endDate;
	public int numRooms;
	public int numAdults;
	public int numChildren;
	public List<Hotel> hotels = new ArrayList<Hotel>();
	
	SearchResult(){}
	
	public SearchResult(SearchParams sp)
	{
		this.startDate = sp.getCheckInDate().toDate();
		this.endDate = sp.getCheckOutDate().toDate();
		this.numRooms = sp.getNumRooms();
		this.numAdults = sp.getNumAdults1() + sp.getNumAdults2() + sp.getNumAdults3() + sp.getNumAdults4();
		this.numChildren = sp.getNumChildren1() + sp.getNumChildren2() + sp.getNumChildren3() + sp.getNumChildren4();
	}
	
	public Date getStartDate()
	{
		return startDate;
	}
	public Date getEndDate()
	{
		return endDate;
	}
	
	public List<Hotel> getHotels()
	{
		return hotels;
	}
	
	public int getNumRooms()
	{
		return numRooms;
	}

	public int getNumAdults()
	{
		return numAdults;
	}

	public int getNumChildren()
	{
		return numChildren;
	}

	public String toString()
	{
		return ToStringBuilder.reflectionToString(this);
	}
}
