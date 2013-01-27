package com.burda.scraper.inventory;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.burda.scraper.dao.HotelDetailCacheKey;
import com.burda.scraper.dao.HotelDetailDAO;
import com.burda.scraper.model.Hotel;
import com.burda.scraper.model.SearchParams;
import com.burda.scraper.model.SearchResult;
import com.burda.scraper.model.SortType;
import com.burda.scraper.model.persisted.HotelDetail;
import com.burda.scraper.model.persisted.InventorySource;
import com.google.code.ssm.api.format.SerializationType;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

public class InventoryServiceImpl implements InventoryService
{	
	private static int SESSION_CACHE_TIMEOUT_IN_SECONDS = (60* 180); /* three hours */
	
  @Autowired
  @Qualifier("defaultMemcachedClient") 
  private com.google.code.ssm.Cache cache;
  
  @Autowired
  private HotelDetailDAO hotelDetailDAO;
  
  private static final Logger logger = LoggerFactory.getLogger(InventoryServiceImpl.class);  
  private static final ExecutorService executor = Executors.newCachedThreadPool();
  
	private NODCWarehouse nodcInventorySource;
	private FrenchQuarterGuideInventorySource fqgInventorySource;
	
	@Override
	public void search(
			final HttpServletRequest request, final SearchParams params) throws Exception
	{		
		List<Callable<Collection<Hotel>>> workers = Lists.newArrayList();
		workers.add(new Callable<Collection<Hotel>>(){

			@Override
			public Collection<Hotel> call() throws Exception
			{
				return nodcInventorySource.getInitialResultsAndAsyncContinue(request, params);
			}});
		workers.add(new Callable<Collection<Hotel>>(){

			@Override
			public Collection<Hotel> call() throws Exception
			{
				return fqgInventorySource.getInitialResultsAndAsyncContinue(request, params);
			}});
		//List<Future<SearchResult>> workerResults = executor.invokeAll(workers, 15, TimeUnit.SECONDS);
		List<Future<Collection<Hotel>>> workerResults = executor.invokeAll(workers);
		workerResults.get(0);
		workerResults.get(1);
		
		Session session = new Session(params);
		session.setCurrentPage(1);
		session.setCurrentSort(SortType.DEFAULT);
		cache.set(params.getSessionInfo().getSessionId(), SESSION_CACHE_TIMEOUT_IN_SECONDS, session, SerializationType.JSON );
	}
	
	@Override
	public SearchResult getAggragatedResults(SessionInfo sessionInfo, SortType sortBy, Integer page) 
	{
		SearchResult sr = null;
		Session s = getFromCache(sessionInfo.getSessionId());
		Multimap<InventorySource, Hotel> rawResults = HashMultimap.create();
		Collection<Hotel> nodcHotels = (Collection<Hotel>)getFromCache(sessionInfo.getSessionId()+InventorySource.NODC.name());
		Collection<Hotel> fqgHotels = (Collection<Hotel>)getFromCache(sessionInfo.getSessionId()+InventorySource.FQG.name());
		if (nodcHotels == null)
			nodcHotels = Lists.newArrayList();
		if (fqgHotels == null)
			fqgHotels = Lists.newArrayList();
		rawResults.putAll(InventorySource.NODC, nodcHotels);
		rawResults.putAll(InventorySource.FQG, fqgHotels);
		if (s != null)
		{
			if (page != null)
				s.setCurrentPage(page);
			if (sortBy != null)
				s.setCurrentSort(sortBy);
			try
			{
				cache.set(sessionInfo.getSessionId(), SESSION_CACHE_TIMEOUT_IN_SECONDS, s, SerializationType.JSON );
			}
			catch (Exception e)
			{
				logger.error("error saving updated session", e);
			}
			sr = s.getSearchResults(rawResults);
		}
		
		return sr;
	}
	
	@Override
	public HotelDetail getHotelDetails(String hotelName)
	{
		return hotelDetailDAO.getHotelDetail(new HotelDetailCacheKey(hotelName));
	}
	
	public void setNODCInventorySource(NODCWarehouse invSource)
	{
		this.nodcInventorySource = invSource;
	}
	
	public void setFQGInventorySource(FrenchQuarterGuideInventorySource invSource)
	{
		this.fqgInventorySource = invSource;
	}	
	
	private final <T> T getFromCache(String key)
	{
		T result = null;
		try
		{
			result = (T)cache.get(key,  SerializationType.JSON);
		}
		catch (Exception e)
		{
			logger.error("unable to retrieve: " + key + " from cache", e);
		}
		return result;
	}
}
