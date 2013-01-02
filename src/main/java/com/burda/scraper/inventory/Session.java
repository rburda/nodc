package com.burda.scraper.inventory;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.burda.scraper.model.Hotel;
import com.burda.scraper.model.SearchParams;
import com.burda.scraper.model.SearchResult;
import com.burda.scraper.model.persisted.InventorySource;
import com.google.common.collect.Maps;

public class Session implements Serializable
{
	private static final long serialVersionUID = 1L;
	private SearchParams params;
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
		return searchResultMap;
	}
	
	public void setSearchResultMap(Map<InventorySource, SearchResult> map)
	{
		this.searchResultMap = map;
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
		if (fqgResult != null)
			aggragatedResult.getHotels().addAll(fqgResult.hotels);
		if (nodcResult != null)
		{
			for (Hotel h: nodcResult.hotels)
			{
				Iterator<Hotel> fqgHotels = aggragatedResult.hotels.iterator();
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
			aggragatedResult.getHotels().addAll(nodcResult.hotels);			
		}
		
		return aggragatedResult;
	}
}