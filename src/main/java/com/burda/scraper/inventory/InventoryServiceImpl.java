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
import com.burda.scraper.model.RoomType;
import com.burda.scraper.model.SearchParams;
import com.burda.scraper.model.SearchResult;
import com.burda.scraper.model.SortType;
import com.burda.scraper.model.persisted.HotelDetail;
import com.burda.scraper.model.persisted.InventorySource;
import com.burda.scraper.model.persisted.RoomTypeDetail;
import com.google.code.ssm.api.format.SerializationType;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

public class InventoryServiceImpl implements InventoryService
{	
	private static int SESSION_CACHE_TIMEOUT_IN_SECONDS = (60* 180); /* three hours */
	
 // @Autowired
 // @Qualifier("defaultMemcachedClient") 
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
				Collection<Hotel> hotels = Lists.newArrayList();
				try
				{
					hotels = nodcInventorySource.getInitialResultsAndAsyncContinue(request, params);
				}
				catch (Throwable t)
				{
					logger.error("unable to complete nodc inv. search", t);
				}
				return hotels;
			}});
		workers.add(new Callable<Collection<Hotel>>(){

			@Override
			public Collection<Hotel> call() throws Exception
			{
				Collection<Hotel> hotels = Lists.newArrayList();
				try
				{
					hotels = fqgInventorySource.getInitialResultsAndAsyncContinue(request, params);
				}
				catch (Throwable t)
				{
					logger.error("Unable to complete fqg inv search", t);
				}
				return hotels;
			}});
		//List<Future<SearchResult>> workerResults = executor.invokeAll(workers, 15, TimeUnit.SECONDS);
		executor.invokeAll(workers);
		
		Session session = new Session(params);
		session.setCurrentPage(1);
		session.setCurrentSort(SortType.DEFAULT);
		logger.debug(String.format("CACHE: session cache key (%1$s);", params.getSessionInfo().getSessionId()));
		request.getSession().setAttribute(params.getSessionInfo().getSessionId(), session);
	}
	
	@Override
	public SearchResult getAggragatedResults(SessionInfo sessionInfo, SortType sortBy, Integer page) 
	{
		SearchResult sr = null;
		Session s = getFromCache(sessionInfo.getRequest(), sessionInfo.getSessionId());
		Multimap<InventorySource, Hotel> rawResults = HashMultimap.create();
		String nodcCacheKey = InventorySource.NODC.name();
		String fqgCacheKey = InventorySource.FQG.name();
		Collection<Hotel> nodcHotels = (Collection<Hotel>)getFromCache(sessionInfo.getRequest(), nodcCacheKey);
		Collection<Hotel> fqgHotels = (Collection<Hotel>)getFromCache(sessionInfo.getRequest(), fqgCacheKey);
		if (nodcHotels == null)
			nodcHotels = Lists.newArrayList();
		logger.debug(String.format("CACHE: nodc cache key (%1$s); num hotels retrieved: " + nodcHotels.size(), nodcCacheKey));
		if (fqgHotels == null)
			fqgHotels = Lists.newArrayList();
		logger.debug(String.format("CACHE: fqg cache key (%1$s); num hotels retrieved: " + fqgHotels.size(), fqgCacheKey));
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
				sessionInfo.getRequest().getSession().setAttribute(sessionInfo.getSessionId(), s);
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
	public HotelDetail getHotelDetails(SessionInfo sessionInfo, String hotelName)
	{
		SearchResult searchResult = getAggragatedResults(sessionInfo,  null,  null);
		HotelDetail hotelDetail = null;
		if (searchResult != null)
		{
			Hotel rateInfo = searchResult.getHotel(hotelName);
			if (rateInfo != null)
			{
				hotelDetail = rateInfo.getHotelDetails();
				for (RoomType rt: rateInfo.getRoomTypes())
				{
					for (RoomTypeDetail roomTypeContent: hotelDetail.getRoomTypeDetails())
					{
						if (roomTypeContent.getName().equals(rt.getName()))
						{
							roomTypeContent.setDailyRates(rt.dailyRates);
							roomTypeContent.setBookItUrl(rt.bookItUrl);
							roomTypeContent.setAvgNightlyRate(rt.avgNightlyRate);
							roomTypeContent.setAvgNightlyOriginalRate(rt.avgNightlyOriginalRate);
							roomTypeContent.setPromoRate(rt.isPromoRate());
						}
					}
				}				
			}
		}
		if (hotelDetail == null)
			hotelDetail = hotelDetailDAO.getHotelDetail(new HotelDetailCacheKey(hotelName));
		
		return hotelDetail;	
	}
	
	public void setNODCInventorySource(NODCWarehouse invSource)
	{
		this.nodcInventorySource = invSource;
	}
	
	public void setFQGInventorySource(FrenchQuarterGuideInventorySource invSource)
	{
		this.fqgInventorySource = invSource;
	}	
	
	private final <T> T getFromCache(HttpServletRequest request, String key)
	{
		T result = null;
		try
		{
			result = (T)request.getSession().getAttribute(key);
		}
		catch (Exception e)
		{
			logger.error("unable to retrieve: " + key + " from cache", e);
		}
		return result;
	}
}
