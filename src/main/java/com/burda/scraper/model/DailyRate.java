package com.burda.scraper.model;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

public class DailyRate
{
	public Date date;
	public BigDecimal price;
	
	public String toString()
	{
		return ToStringBuilder.reflectionToString(this);
	}
}
