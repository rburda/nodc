package com.burda.scraper.inventory;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.burda.scraper.model.SearchParams;
import com.burda.scraper.model.SearchResult;
import com.burda.scraper.model.SortType;
import com.burda.scraper.model.persisted.InventorySource;
import com.google.code.ssm.api.format.SerializationType;
import com.google.common.collect.Lists;

public class InventoryServiceImpl implements InventoryService
{	
	private static int SESSION_CACHE_TIMEOUT_IN_SECONDS = (60* 180); /* three hours */
	
  @Autowired
  @Qualifier("defaultMemcachedClient") 
  private com.google.code.ssm.Cache cache;
  
  private static final Logger logger = LoggerFactory.getLogger(InventoryServiceImpl.class);
  
	private NODCWarehouse nodcInventorySource;
	private FrenchQuarterGuideInventorySource fqgInventorySource;
	
	@Override
	public void search(
			final HttpServletRequest request, final SearchParams params) throws Exception
	{
		ExecutorService executor = Executors.newFixedThreadPool(2);
		
		List<Callable<SearchResult>> workers = Lists.newArrayList();
		workers.add(new Callable<SearchResult>(){

			@Override
			public SearchResult call() throws Exception
			{
				return nodcInventorySource.getResults(request, params);
			}});
		workers.add(new Callable<SearchResult>(){

			@Override
			public SearchResult call() throws Exception
			{
				return fqgInventorySource.getResults(request, params);
			}});
		//List<Future<SearchResult>> workerResults = executor.invokeAll(workers, 15, TimeUnit.SECONDS);
		List<Future<SearchResult>> workerResults = executor.invokeAll(workers);

		Session session = new Session(params);
		Future<SearchResult> nodcResult = workerResults.get(0);
		Future<SearchResult> fqgResult = workerResults.get(1);
		if (nodcResult.isCancelled())
			logger.warn("Unable to get NODC results before timeout");
		else
			session.addToResults(InventorySource.NODC, nodcResult.get());	
		if (fqgResult.isCancelled())
			logger.warn("Unable to get FQG results before timeout");
		else
			session.addToResults(InventorySource.FQG, fqgResult.get());	

		executor.shutdown();
		session.setCurrentPage(1);
		session.setCurrentSort(SortType.DEFAULT);
		cache.set(params.getSessionId(), SESSION_CACHE_TIMEOUT_IN_SECONDS, session, SerializationType.JSON );
	}
	
	@Override
	public SearchResult getUpdatedResults(String sessionId, SortType sortBy, Integer page) 
	{
		SearchResult sr = null;
		Session s = null;
		try
		{
			s = cache.get(sessionId,  SerializationType.JSON);
		}
		catch (Exception e)
		{
			logger.error("Unable to retrieve session: " + sessionId + " from cache", e);
		}
		
		if (s != null)
		{
			if (page != null)
				s.setCurrentPage(page);
			if (sortBy != null)
				s.setCurrentSort(sortBy);
			try
			{
				cache.set(sessionId, SESSION_CACHE_TIMEOUT_IN_SECONDS, s, SerializationType.JSON );
			}
			catch (Exception e)
			{
				logger.error("error saving updated session", e);
			}
			sr = s.getSearchResults();
		}
		
		return sr;
	}
	
	public void setNODCInventorySource(NODCWarehouse invSource)
	{
		this.nodcInventorySource = invSource;
	}
	
	public void setFQGInventorySource(FrenchQuarterGuideInventorySource invSource)
	{
		this.fqgInventorySource = invSource;
	}	
}
