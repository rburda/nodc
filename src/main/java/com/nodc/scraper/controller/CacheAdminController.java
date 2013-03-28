package com.nodc.scraper.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nodc.scraper.cache.FrenchQuarterGuideCacheLoader;
import com.nodc.scraper.cache.NODCHotelLoader;
import com.nodc.scraper.cache.OneTimeHotelLoader;

@Controller
@RequestMapping("/cacheadmin")
@Profile("scheduleTasks")
public class CacheAdminController
{
	private final NODCHotelLoader nodcHotelLoader;
	private final FrenchQuarterGuideCacheLoader fqgCacheLoader;
	private final OneTimeHotelLoader fqgFirstStepLoader;
	
	@Autowired
	public CacheAdminController(
			NODCHotelLoader nodcLoader, 
			FrenchQuarterGuideCacheLoader fqgCacheLoader,
			OneTimeHotelLoader fqgFirstStepLoader)
	{
		this.nodcHotelLoader = nodcLoader;
		this.fqgCacheLoader = fqgCacheLoader;
		this.fqgFirstStepLoader = fqgFirstStepLoader;
	}
	
	@RequestMapping(value="/nodc", method = {RequestMethod.GET, RequestMethod.POST})
	public @ResponseBody void reloadNodc(@RequestParam(value="hotel", required=false) String hotel) throws Exception
	{
		nodcHotelLoader.loadCache(hotel);
	}
	
	@RequestMapping(value="/fqg", method = {RequestMethod.GET, RequestMethod.POST})
	public @ResponseBody void reloadFqg(@RequestParam(value="hotel", required=false) String hotel) throws Exception
	{
		fqgCacheLoader.loadCache(hotel);
	}
	
	@RequestMapping(value="/fqgFirst", method = {RequestMethod.GET, RequestMethod.POST})
	public @ResponseBody void reloadFqgFirst(@RequestParam(value="hotel", required=false) String hotel) throws Exception
	{
		fqgFirstStepLoader.initializeHotelData(hotel);
	}	
	
	@RequestMapping(value="/all", method = {RequestMethod.GET, RequestMethod.POST})
	public @ResponseBody void reloadAll(@RequestParam(value="hotel", required=false) String hotel) throws Exception
	{
		reloadNodc(hotel);
		reloadFqg(hotel);
		reloadFqgFirst(hotel);
	}	
}