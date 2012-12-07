package com.burda.scraper.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonIgnore;


public class SearchResult
{
	public Date startDate;
	public Date endDate;
	public List<Hotel> hotels = new ArrayList<Hotel>();
	@JsonIgnore
	public List<Header> headers = new ArrayList<Header>();
	
	public Date getStartDate()
	{
		return startDate;
	}
	public Date getEndDate()
	{
		return endDate;
	}
	
	public List<Hotel> getHotels()
	{
		return hotels;
	}
	
	public String toString()
	{
		return ToStringBuilder.reflectionToString(this);
	}
}
