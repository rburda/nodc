package com.burda.scraper.inventory;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.burda.scraper.model.SearchResult;

public class InventoryServiceImpl implements InventoryService
{
	@Override
	public SearchResult getSearchResult() throws Exception
	{
		ExecutorService executor = Executors.newSingleThreadExecutor();
		Future<SearchResult> task = executor.submit(new Callable<SearchResult>(){

			@Override
			public SearchResult call() throws Exception
			{
				return new NODCInventorySource().getResults();
			}});
		
		executor.awaitTermination(10, TimeUnit.SECONDS);
		SearchResult result = new SearchResult();
		result.getHotels().addAll(task.get().hotels);
		result.endDate = task.get().endDate;
		result.startDate = task.get().startDate;
		return result;
	}
	
}
