package com.burda.scraper.inventory;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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

public class InventoryServiceImpl implements InventoryService
{	
  @Autowired
  @Qualifier("defaultMemcachedClient") 
  private com.google.code.ssm.Cache cache;
  
  private static final Logger logger = LoggerFactory.getLogger(InventoryServiceImpl.class);
  
	private NODCWarehouse nodcInventorySource;
	private FrenchQuarterGuideInventorySource fqgInventorySource;
	
	@Override
	public SearchResult getSearchResult(
			final HttpServletRequest request, final SearchParams params) throws Exception
	{
		ExecutorService executor = Executors.newFixedThreadPool(2);
		Future<SearchResult> nodcTask = executor.submit(new Callable<SearchResult>(){

			@Override
			public SearchResult call() throws Exception
			{
				return nodcInventorySource.getResults(request, params);
			}});
		Future<SearchResult> fqgTask = executor.submit(new Callable<SearchResult>(){

			@Override
			public SearchResult call() throws Exception
			{
				return fqgInventorySource.getResults(request, params);
			}});		
		
		executor.shutdown();

		Session session = new Session(params);
		session.addToResults(InventorySource.NODC, nodcTask.get());
		session.addToResults(InventorySource.FQG, fqgTask.get());
		cache.set(params.getSessionId(), (60 * 180) /*three hours*/, session, SerializationType.JSON );
		return session.getSearchResults(1, SortType.DEFAULT);
	}
	
	@Override
	public SearchResult getUpdatedResults(String sessionId, SortType sortBy, int page) 
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

			sr = s.getSearchResults(page, sortBy);
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
