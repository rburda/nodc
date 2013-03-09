package com.nodc.scraper.cache;

import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.AbstractScheduledService;
import com.nodc.scraper.dao.CacheStateDAO;
import com.nodc.scraper.model.persisted.CacheState;

public abstract class AbstractCacheRefresher extends AbstractScheduledService
{
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final CacheStateDAO cacheStateDAO;
	private final String cacheStateName;
	private DateTime lastRefreshed = null;

	protected AbstractCacheRefresher(String cacheStateName, CacheStateDAO csDAO)
	{
		this.cacheStateDAO = csDAO;
		this.cacheStateName = cacheStateName;
	}
	
	@Override
	public void startUp()
	{
		logger.error("starting " + cacheStateName + " cache refresher");
		//loadCache();
	}
	
	@Override
	protected void runOneIteration() throws Exception
	{
		try
		{
			CacheState cs = cacheStateDAO.getCacheState(cacheStateName);
			logger.error("running cache refresher");
			if (lastRefreshed == null || lastRefreshed.isBefore(cs.getLastUpdateDateTime()))
			{
				logger.debug(
						String.format("cache recently updated: lastRefreshed = %1$s, updated = %2$s", 
								lastRefreshed, cs.getLastUpdateDateTimeString()));
				loadCache();
				lastRefreshed = cs.getLastUpdateDateTime();
			}			
		}
		catch (Throwable t)
		{
			logger.error("Unable to refresh cache", t);
		}
	}

	@Override
	protected Scheduler scheduler()
	{
		return Scheduler.newFixedRateSchedule(0, 30, TimeUnit.SECONDS);
	}		

	protected abstract void loadCache();
}
