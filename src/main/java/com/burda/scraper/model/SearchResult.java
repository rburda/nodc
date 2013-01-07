package com.burda.scraper.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonIgnore;

import com.google.common.collect.Lists;


public class SearchResult
{
	public Date startDate;
	public Date endDate;
	public int numRooms;
	public int numAdults;
	public int numChildren;
	public int currentPage = 1;
	public int numPages = 1;
	public int startHotel = 1;
	public int numTotalHotels = 0;
	public SortType currentSort = SortType.DEFAULT;
	private List<Hotel> hotels = new ArrayList<Hotel>();
	
	SearchResult(){}
	
	public SearchResult(SearchParams sp)
	{
		this.startDate = sp.getCheckInDate().toDate();
		this.endDate = sp.getCheckOutDate().toDate();
		this.numRooms = sp.getNumRooms();
		this.numAdults = sp.getNumAdults1() + sp.getNumAdults2() + sp.getNumAdults3() + sp.getNumAdults4();
		this.numChildren = sp.getNumChildren1() + sp.getNumChildren2() + sp.getNumChildren3() + sp.getNumChildren4();
		this.currentPage = 1;
	}
	
	public Date getStartDate()
	{
		return startDate;
	}
	public Date getEndDate()
	{
		return endDate;
	}
	
	@JsonIgnore
	public List<Hotel> getAllHotels()
	{
		return Lists.newArrayList(hotels);
	}
	
	public List<Hotel> getHotels()
	{
		return Lists.newArrayList(hotels);
	}
	
	public void setHotels(List<Hotel> hotels)
	{
		this.hotels = Lists.newArrayList(hotels);
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
	
	public int getNumPages()
	{
		return numPages;
	}
	
	public int getCurrentPage()
	{
		return currentPage;
	}
	
	public SortType getCurrentSort()
	{
		return currentSort;
	}
	
	public int getNumTotalHotels()
	{
		return numTotalHotels;
	}
	
	public int getStartHotel()
	{
		return startHotel;
	}
	
	public int getEndHotel()
	{
		return getStartHotel()+getHotels().size()-1;
	}
	
	public String toString()
	{
		return ToStringBuilder.reflectionToString(this);
	}
}
