package com.burda.scraper.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;


public class RoomType
{
	public String name;
	public String promoDesc;
	public String bookItUrl;
	public BigDecimal avgNightlyRate;
	public BigDecimal totalPrice;
	public List<DailyRate> dailyRates = new ArrayList<DailyRate>();
	
	public String toString()
	{
		return ToStringBuilder.reflectionToString(this);
	}
}