package com.burda.scraper.inventory;

import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.burda.scraper.model.Hotel;
import com.burda.scraper.model.SearchParams;
import com.burda.scraper.model.SearchResult;

public class InventoryServiceImpl implements InventoryService
{
	private NODCInventorySource nodcInventorySource;
	private FrenchQuarterGuideInventorySource fqgInventorySource;
	
	@Override
	public SearchResult getSearchResult(
			final HttpServletRequest request, final SearchParams params) throws Exception
	{
		ExecutorService executor = Executors.newFixedThreadPool(2);
		Future<SearchResult> task = executor.submit(new Callable<SearchResult>(){

			@Override
			public SearchResult call() throws Exception
			{
				return nodcInventorySource.getResults(request, params);
			}});
		Future<SearchResult> task1 = executor.submit(new Callable<SearchResult>(){

			@Override
			public SearchResult call() throws Exception
			{
				return fqgInventorySource.getResults(request, params);
			}});		
		
		executor.shutdown();
		SearchResult result = new SearchResult();
		result.getHotels().addAll(task1.get().hotels);
		for (Hotel h: task.get().hotels)
		{
			Iterator<Hotel> fqgHotels = result.hotels.iterator();
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
		result.getHotels().addAll(task.get().hotels);
		
		result.headers.addAll(task.get().headers);
		result.headers.addAll(task1.get().headers);
		result.endDate = task.get().endDate;
		result.startDate = task.get().startDate;
		return result;
	}
	
	public void setNODCInventorySource(NODCInventorySource invSource)
	{
		this.nodcInventorySource = invSource;
	}
	
	public void setFQGInventorySource(FrenchQuarterGuideInventorySource invSource)
	{
		this.fqgInventorySource = invSource;
	}	
}
