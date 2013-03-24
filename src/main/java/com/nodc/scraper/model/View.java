package com.nodc.scraper.model;

/**
 * Represents a configuration of how the user wants to view the hotel results
 * Currently consists of a SortType, a page, a filterLocation and/or a 
 * hotelName
 * @author rburda
 *
 */
public class View
{
	public static final View INITIAL()
	{
		return new View(SortType.DEFAULT_D, 1, null, null);
	}
	
	public static final class ViewBuilder
	{
		private SortType st = null;
		private Integer pg = null;
		private String fLoc = null;
		private String fHn = null;
		
		public ViewBuilder withSortType(SortType st)
		{
			this.st = st;
			return this;
		}
		
		public ViewBuilder withPage(Integer pg)
		{
			this.pg = pg;
			return this;
		}
		
		public ViewBuilder withFilterLocation(String fLoc)
		{
			this.fLoc = fLoc;
			return this;
		}
		
		public ViewBuilder withFilterHotelName(String fHn)
		{
			this.fHn = fHn;
			return this;
		}
		
		public View create()
		{
			return new View(st, pg, fLoc, fHn);
		}
	}
	
	private final SortType sortType;
	private final Integer page;
	private final String filterLocation;
	private final String filterHotelName;
	
	private View(SortType st, Integer pg, String fLoc, String fHn)
	{
		this.sortType = st;
		this.page = pg;
		this.filterLocation = fLoc;
		this.filterHotelName = fHn;
	}

	public SortType getSortType()
	{
		return sortType;
	}

	public Integer getPage()
	{
		return page;
	}

	public String getFilterLocation()
	{
		return filterLocation;
	}

	public String getFilterHotelName()
	{
		return filterHotelName;
	}
	
	
	
}
