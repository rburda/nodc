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
	private static final int NUM_RESULTS_PER_PAGE = 20;	
	
	public Date startDate;
	public Date endDate;
	public int numRooms;
	public int numAdults;
	public int numChildren;
	public int currentPage;
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
		int startResult = ( (currentPage-1)*NUM_RESULTS_PER_PAGE + 1 );
		int endResult = startResult+NUM_RESULTS_PER_PAGE;
		if (startResult > hotels.size()-1)
			startResult = hotels.size()-1;
		if (endResult > hotels.size()-1)
			endResult = hotels.size()-1;
		
		List<Hotel> sortedHotels = Lists.newArrayList(hotels);
		Collections.sort(sortedHotels, currentSort);
		
		List<Hotel> pagedHotels = Lists.newArrayList(sortedHotels.subList(startResult, endResult));
		return pagedHotels;
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
	
	public int getNumResultPages()
	{
		return (int) Math.ceil(hotels.size() / NUM_RESULTS_PER_PAGE);
	}
	
	public int getCurrentPage()
	{
		return currentPage;
	}

	public String toString()
	{
		return ToStringBuilder.reflectionToString(this);
	}
}
