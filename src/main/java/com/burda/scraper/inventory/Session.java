package com.burda.scraper.inventory;

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.burda.scraper.model.Hotel;
import com.burda.scraper.model.SearchParams;
import com.burda.scraper.model.SearchResult;
import com.burda.scraper.model.SortType;
import com.burda.scraper.model.persisted.InventorySource;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class Session implements Serializable
{
	private static final int NUM_RESULTS_PER_PAGE = 20;	
	private static final long serialVersionUID = 1L;
	
	private SearchParams params;
	private Map<InventorySource, Boolean> completeStatusMap = Maps.newConcurrentMap();
	private Map<InventorySource, SearchResult> searchResultMap = Maps.newConcurrentMap();
	private SortType currentSort;
	private int currentPage = 1;
	private boolean sortAsc;

	Session()
	{}
	
	public Session(SearchParams params)
	{
		this.params = params;
	}
	
	public SearchParams getSearchParams()
	{
		return params;
	}
	
	public void setSearchParams(SearchParams params)
	{
		this.params = params;
	}
	
	public Map<InventorySource, SearchResult> getSearchResultMap()
	{
		return Maps.newHashMap(searchResultMap);
	}
	
	public void setSearchResultMap(Map<InventorySource, SearchResult> map)
	{
		this.searchResultMap.clear();
		this.searchResultMap.putAll(map);
	}
	
	public Map<InventorySource, Boolean> getCompleteStatusMap()
	{
		return Maps.newHashMap(completeStatusMap);
	}
	
	public void setCompleteStatusMap(Map<InventorySource, Boolean> map)
	{
		this.completeStatusMap.clear();
		this.completeStatusMap.putAll(map);
	}
	
	public int getCurrentPage()
	{
		return currentPage;
	}
	
	public void setCurrentPage(int page)
	{
		this.currentPage = page;
	}
	
	public boolean getSortDirection()
	{
		return sortAsc;
	}
	
	public void setSortDirection(boolean sd)
	{
		this.sortAsc = sd;
	}

	public SortType getCurrentSortCache()
	{
		return currentSort;
	}
	
	public void setCurrentSortCache(SortType st)
	{
		currentSort = st;
	}
	
	@JsonIgnore
	public SortType getCurrentSort()
	{
		return currentSort;
	}	
	
	@JsonIgnore
	public void setCurrentSort(SortType sortType)
	{
		if (sortType == currentSort)
			sortAsc = !sortAsc;
		else
			sortAsc = true;
		this.currentSort = sortType;			
	}
	
	@JsonIgnore
	public final void addToResults(InventorySource iSource, SearchResult results)
	{
		searchResultMap.put(iSource,  results);
	}
		
	@JsonIgnore
	public final SearchResult getSearchResults()
	{	
		SearchResult aggragatedResult = new SearchResult(params);
		SearchResult fqgResult = searchResultMap.get(com.burda.scraper.model.persisted.InventorySource.FQG);
		SearchResult nodcResult = searchResultMap.get(com.burda.scraper.model.persisted.InventorySource.NODC);
		List<Hotel> aggragatedHotels = Lists.newArrayList();
		if (fqgResult != null)
			aggragatedHotels.addAll(fqgResult.getAllHotels());
		if (nodcResult != null)
		{
			for (Hotel h: nodcResult.getAllHotels())
			{
				Iterator<Hotel> fqgHotels = aggragatedHotels.iterator();
				boolean found = false;
				while (fqgHotels.hasNext() && !found)
				{
					Hotel fqgHotel = fqgHotels.next();
					if (fqgHotel.getName().equals(h.getName()))
					{
						fqgHotels.remove();
						found = true;
					}
				}
			}
			aggragatedHotels.addAll(nodcResult.getAllHotels());	
		}
		
		aggragatedResult.currentPage = currentPage;
		aggragatedResult.numPages = (int) Math.ceil(((float)aggragatedHotels.size()) / NUM_RESULTS_PER_PAGE);
		aggragatedResult.startHotel = getStartResult(aggragatedHotels)+1;
		aggragatedResult.currentSort = currentSort;
		aggragatedResult.numTotalHotels = aggragatedHotels.size();
		aggragatedResult.setHotels(createFilteredHotelList(aggragatedHotels));
		return aggragatedResult;
	}
	
	private List<Hotel> createFilteredHotelList(List<Hotel> hotels)
	{
		int startResult = getStartResult(hotels);
		int endResult = startResult+NUM_RESULTS_PER_PAGE;
		
		if (endResult > hotels.size())
			endResult = hotels.size();
		
		List<Hotel> sortedHotels = Lists.newArrayList(hotels);
		Collections.sort(sortedHotels, currentSort);
		if (!sortAsc)
			Collections.reverse(sortedHotels);
		
		List<Hotel> pagedHotels = Collections.EMPTY_LIST;
		if (startResult >=0)
			pagedHotels = Lists.newArrayList(sortedHotels.subList(startResult, endResult));
		return pagedHotels;
	}
	
	private int getStartResult(List<Hotel> allHotels)
	{
		int startResult = ( (currentPage-1)*NUM_RESULTS_PER_PAGE );
		if (startResult > allHotels.size()-1)
			startResult = allHotels.size()-1;
		return startResult;
	}
}