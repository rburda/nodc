package com.burda.scraper.dao;

import java.io.Serializable;

import com.google.code.ssm.api.CacheKeyMethod;

/*
 * hotelName can have spaces in it, which is considered invalid by 
 * spymemcached client. See: http://code.google.com/p/spymemcached/issues/detail?id=213
 */
public class HotelDetailCacheKey implements Serializable
{
	private static final long serialVersionUID = 1L;
	private String hotelName;
	
	public HotelDetailCacheKey(String n)
	{
		this.hotelName = n;
	}
	
	String getHotelName()
	{
		return hotelName;
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
		return hotelName.equals(other.hotelName);
	}
	
	public int hashCode()
	{
		return hotelName.hashCode();
	}
}