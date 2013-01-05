package com.burda.scraper.inventory;

import java.io.Serializable;
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
	private static final long serialVersionUID = 1L;

	
	private SearchParams params;
	private Map<InventorySource, Boolean> completeStatusMap = Maps.newConcurrentMap();
	private Map<InventorySource, SearchResult> searchResultMap = Maps.newConcurrentMap();

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
	
	@JsonIgnore
	public final void addToResults(InventorySource iSource, SearchResult results)
	{
		searchResultMap.put(iSource,  results);
	}
		
	@JsonIgnore
	public final SearchResult getSearchResults(int page, SortType sort)
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
			aggragatedResult.setHotels(aggragatedHotels);
			aggragatedResult.currentPage = page;
			aggragatedResult.currentSort = sort;
		}
		
		return aggragatedResult;
	}
}