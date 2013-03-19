package com.nodc.scraper.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonIgnore;

import com.google.common.collect.Lists;


public class SearchResult
{
	private SearchParams searchParams;
	public Date startDate;
	public Date endDate;
	public int numRooms;
	public int numAdults;
	public int numChildren;
	public int currentPage = 1;
	public int numPages = 1;
	public int startHotel = 1;
	public int numTotalHotels = 0;
	public SortType currentSort = SortType.DEFAULT_A;
	public String currentFilterLocation = null;
	
	private List<String> locations = Lists.newArrayList();
	
	//all hotels (unfiltered)
	private List<Hotel> allHotels = new ArrayList<Hotel>();
	//a subset of the filteredSet of hotels;
	private List<Hotel> pagedSubsetHotels = new ArrayList<Hotel>();
	
	SearchResult(){}
	
	public SearchResult(SearchParams sp)
	{
		this.searchParams = sp;
		this.startDate = sp.getCheckInDate().toDate();
		this.endDate = sp.getCheckOutDate().toDate();
		this.numRooms = sp.getNumRooms();
		int nA = sp.getNumAdults1();
		int nC = sp.getNumChildren1();
		if (numRooms > 1)
		{
			nA += sp.getNumAdults2();
			nC += sp.getNumChildren2();
		}
		if (numRooms > 2)
		{
			nA += sp.getNumAdults3();
			nC += sp.getNumChildren3();
		}
		if (numRooms > 3)
		{
			nA += sp.getNumAdults4();
			nC += sp.getNumChildren4();
		}
		this.numAdults = nA;
		this.numChildren = nC;
		this.currentPage = 1;
	}
	
	public SearchParams getSearchParams()
	{
		return searchParams;
	}
	
	public Date getStartDate()
	{
		return startDate;
	}
	public Date getEndDate()
	{
		return endDate;
	}
	
	public List<Hotel> getAllHotels()
	{
		return Lists.newArrayList(allHotels);
	}
	
	public void setAllHotels(List<Hotel> allHotels)
	{
		this.allHotels = allHotels;
	}
	
	public List<Hotel> getPagedSubsetHotels()
	{
		return Lists.newArrayList(pagedSubsetHotels);
	}
	
	public Hotel getHotel(String hotelName)
	{
		Hotel found = null;
		for (Hotel h: getAllHotels())
		{
			if (h.getName().equals(hotelName))
				found = h;
		}
		return found;
	}
	
	public void setPagedSubsetHotels(List<Hotel> hotels)
	{
		this.pagedSubsetHotels = Lists.newArrayList(hotels);
	}
	
	public List<String> getLocations()
	{
		return locations;
	}
	
	public void setLocations(List<String> locations)
	{
		this.locations = Lists.newArrayList(locations);
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
	
	public String getCurrentFilterLocation()
	{
		return currentFilterLocation;
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
		return getStartHotel()+pagedSubsetHotels.size()-1;
	}
	
	public boolean isPreferredHotelRequested()
	{
		return !StringUtils.isEmpty(getSearchParams().getPreferredProductName());
	}
	
	public boolean isPreferredHotelAvailable()
	{
		boolean avail = false;
		if (isPreferredHotelRequested())
		{
			for (Hotel h: getAllHotels())
				if (h.getName().equals(getSearchParams().getPreferredProductName()))
					avail = true;
		}
		return avail;
	}
	
	public String getPreferredHotel()
	{
		return getSearchParams().getPreferredProductName();
	}
	
	public String toString()
	{
		return ToStringBuilder.reflectionToString(this);
	}
}

	