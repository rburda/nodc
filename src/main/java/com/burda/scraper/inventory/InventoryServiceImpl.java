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
		ExecutorService executor = Executors.newFixedThreadPool(2);
		Future<SearchResult> task = executor.submit(new Callable<SearchResult>(){

			@Override
			public SearchResult call() throws Exception
			{
				return new NODCInventorySource().getResults();
			}});
		Future<SearchResult> task1 = executor.submit(new Callable<SearchResult>(){

			@Override
			public SearchResult call() throws Exception
			{
				return new FrenchQuarterGuideInventorySource().getResults();
			}});		
		
		executor.shutdown();
		SearchResult result = new SearchResult();
		result.getHotels().addAll(task.get().hotels);
		result.getHotels().addAll(task1.get().hotels);
		result.headers.addAll(task.get().headers);
		result.headers.addAll(task1.get().headers);
		result.endDate = task.get().endDate;
		result.startDate = task.get().startDate;
		return result;
	}
	
}
