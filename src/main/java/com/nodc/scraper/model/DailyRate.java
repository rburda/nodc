package com.nodc.scraper.model;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

public class DailyRate
{
	public Date date;
	public BigDecimal price;
	public BigDecimal originalPrice;
	
	public Date getDate()
	{
		return date;
	}
	
	public BigDecimal getPrice()
	{
		return price;
	}
	
	public BigDecimal getOriginalPrice()
	{
		return originalPrice;
	}
	
	public String toString()
	{
		return ToStringBuilder.reflectionToString(this);
	}
}
