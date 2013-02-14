package com.nodc.scraper.dao;

import java.io.Serializable;

import com.nodc.scraper.model.persisted.InventorySource;
import com.google.code.ssm.api.CacheKeyMethod;

/*
 * hotelName can have spaces in it, which is considered invalid by 
 * spymemcached client. See: http://code.google.com/p/spymemcached/issues/detail?id=213
 */
public class HotelDetailCacheKey implements Serializable
{
	private static final long serialVersionUID = 1L;
	private String hotelName;
	private InventorySource is;
	
	public HotelDetailCacheKey(String n, InventorySource is)
	{
		this.hotelName = n;
		this.is = is;
	}
	
	String getHotelName()
	{
		return hotelName;
	}
	
	InventorySource getInventorySource()
	{
		return is;
	}
	
	@CacheKeyMethod
	public String getCacheKey()
	{
		return hotelName.replace(' ', '_');
	}
	
	public boolean equals(Object o)
	{
		if (o == null)
			return false;
		
		if (!(o instanceof HotelDetailCacheKey))
			return false;
		
		HotelDetailCacheKey other = (HotelDetailCacheKey)o;
		if (hotelName.equals(other.hotelName))
			if (is.equals(other.is))
				return true;
		return false;
	}
	
	public int hashCode()
	{
		return hotelName.hashCode();
	}
}