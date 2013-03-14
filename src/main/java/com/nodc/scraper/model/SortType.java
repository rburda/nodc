package com.nodc.scraper.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Comparator;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;

public enum SortType implements Comparator<Hotel>, Serializable
{
	DEFAULT 
	{
		@JsonIgnore
		@Override
		public int compare(Hotel h1, Hotel h2)
		{
			return  (h1.getHotelDetails().getWeight() - h2.getHotelDetails().getWeight());
		}
	},
	PRICE 
	{
		@JsonIgnore
		@Override
		public int compare(Hotel h1, Hotel h2)
		{
			return h1.getLowestAvgRate().compareTo(h2.getLowestAvgRate());
		}
	},
	HOTEL_NAME 
	{
		@JsonIgnore
		@Override
		public int compare(Hotel h1, Hotel h2)
		{
			return h1.getName().compareTo(h2.getName());
		}
	}, 
	RATING 
	{
		@JsonIgnore
		@Override
		public int compare(Hotel h1, Hotel h2)
		{
			float result =  h1.getHotelDetails().getRating() - h2.getHotelDetails().getRating();
			if (result < 0)
				return -1;
			if (result > 0)
				return 1;
			return 0;
		}
	},
	AREA
	{
		@JsonIgnore
		@Override
		public int compare(Hotel h1, Hotel h2)
		{
			String area1 = StringUtils.defaultIfEmpty(h1.getHotelDetails().getAreaDescription(),"ZZZ");
			String area2 = StringUtils.defaultIfEmpty(h2.getHotelDetails().getAreaDescription(),"ZZZ");
			return area1.compareTo(area2);
		}
	};

	@JsonIgnore
	@Override
	public abstract int compare(Hotel h1, Hotel h2);
}
