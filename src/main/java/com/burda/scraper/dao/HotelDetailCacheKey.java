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
}