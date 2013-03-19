package com.nodc.scraper.inventory;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nodc.scraper.model.Hotel;
import com.nodc.scraper.model.SearchParams;
import com.nodc.scraper.model.SearchResult;
import com.nodc.scraper.model.SortType;
import com.nodc.scraper.model.persisted.InventorySource;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

public class Session implements Serializable
{
	private static Logger logger = LoggerFactory.getLogger(Session.class);
	private static final int NUM_RESULTS_PER_PAGE = 20;
	private static final String FILTER_NONE = "NONE";
	private static final long serialVersionUID = 1L;
	
	private SearchParams params;
	private Map<InventorySource, Boolean> completeStatusMap = Maps.newConcurrentMap();
	private SortType currentSort;
	private String filterLocation = FILTER_NONE;
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
		/*
		if (sortType == currentSort)
			sortAsc = !sortAsc;
		else
			sortAsc = true;
	*/
		this.currentSort = sortType;			
	}
	
	@JsonIgnore
	public void setFilterLocation(String loc)
	{
		this.filterLocation = loc;
	}
		
	@JsonIgnore
	public final SearchResult getSearchResults(Multimap<InventorySource, Hotel> rawResults)
	{	
		SearchResult aggragatedResult = new SearchResult(params);
		Collection<Hotel> fqgResult = rawResults.get(com.nodc.scraper.model.persisted.InventorySource.FQG);
		Collection<Hotel> nodcResult = rawResults.get(com.nodc.scraper.model.persisted.InventorySource.NODC);
		logger.debug("num fqg hotels before aggragation: " + (fqgResult == null ? "0" : fqgResult.size()));
		logger.debug("num nodc hotels before aggragation: " + (nodcResult == null ? "0" : nodcResult.size()));
		List<Hotel> aggragatedHotels = Lists.newArrayList();
		if (fqgResult != null)
		{
			for (Hotel h: fqgResult)
				if (!h.getRoomTypes().isEmpty())
					aggragatedHotels.add(h);
				else
					logger.debug("removing fqg hotel: " + h.getName() + " because no room types");
		}
		if (nodcResult != null)
		{
			Iterator<Hotel> nodcHotels = nodcResult.iterator();
			while (nodcHotels.hasNext())
			{
				Hotel h = nodcHotels.next();
				if (h.getRoomTypes().isEmpty())
				{
					logger.debug("removing nodc hotel: " + h.getName() + " because no room types");
					nodcHotels.remove();
					continue;
				}
				
				Iterator<Hotel> fqgHotels = aggragatedHotels.iterator();
				boolean found = false;
				while (fqgHotels.hasNext() && !found)
				{
					Hotel fqgHotel = fqgHotels.next();
					if (fqgHotel.getName().equals(h.getName()))
					{
						logger.debug("removing fqg hotel: " + fqgHotel.getName() + " because match of nodc hotel");
						fqgHotels.remove();
						found = true;
					}
				}
			}
			aggragatedHotels.addAll(nodcResult);
			logger.debug("num hotels after aggragation: " + aggragatedHotels.size());
		}
		
		//filter aggragatedHotels;
		List<Hotel> filteredHotels = Lists.newArrayList(Iterables.filter(aggragatedHotels, new Predicate<Hotel>()
				{
					@Override
					public boolean apply(Hotel h)
					{
						boolean apply = true;
						if (filterLocation != null && !filterLocation.equals(FILTER_NONE))
							if (!StringUtils.equals(filterLocation, h.getHotelDetails().getAreaDescription()))
								apply = false;
						return apply;
					}
				}));
		
		
		aggragatedResult.currentPage = currentPage;
		aggragatedResult.numPages = (int) Math.ceil(((float)filteredHotels.size()) / NUM_RESULTS_PER_PAGE);
		aggragatedResult.startHotel = getStartResult(filteredHotels)+1;
		aggragatedResult.currentSort = currentSort;
		aggragatedResult.currentFilterLocation = filterLocation;
		aggragatedResult.numTotalHotels = filteredHotels.size();
		
		Collections.sort(aggragatedHotels, SortType.HOTEL_NAME_A);
		aggragatedResult.setAllHotels(aggragatedHotels);
		aggragatedResult.setPagedSubsetHotels(createdPagedSubsetHotelList(filteredHotels));
		
		//finally set possible locations
		Set<String> locations = Sets.newHashSet();
		for (Hotel h: aggragatedHotels)
			if (!StringUtils.isEmpty(h.getHotelDetails().getAreaDescription()))
					locations.add(h.getHotelDetails().getAreaDescription());
		
		List<String> sortedLocations = Lists.newArrayList(locations);
		Collections.sort(sortedLocations);
		aggragatedResult.setLocations(sortedLocations);
		return aggragatedResult;
	}
	
	private List<Hotel> createdPagedSubsetHotelList(List<Hotel> hotels)
	{
		int startResult = getStartResult(hotels);
		int endResult = startResult+NUM_RESULTS_PER_PAGE;
		
		if (endResult > hotels.size())
			endResult = hotels.size();
		
		List<Hotel> sortedHotels = Lists.newArrayList(hotels);
		Collections.sort(sortedHotels, currentSort);
		//if (!sortAsc)
		//	Collections.reverse(sortedHotels);
		
		if (currentSort.equals(SortType.DEFAULT_D) /*&& sortAsc*/)
		{
			if (!StringUtils.isEmpty(params.getPreferredProductId()))
			{
				Iterator<Hotel> hotelItor = sortedHotels.iterator();
				while (hotelItor.hasNext())
				{
					Hotel h = hotelItor.next();
					if (h.getName().equals(params.getPreferredProductName()))
					{
						hotelItor.remove();
						sortedHotels.add(0, h);
						break;
					}
				}
			}
		}
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