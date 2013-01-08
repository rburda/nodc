package com.burda.scraper.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Comparator;

import org.codehaus.jackson.annotate.JsonIgnore;

public enum SortType implements Comparator<Hotel>, Serializable
{
	DEFAULT 
	{
		@JsonIgnore
		@Override
		public int compare(Hotel h1, Hotel h2)
		{
			return 0;
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
	};

	@JsonIgnore
	@Override
	public abstract int compare(Hotel h1, Hotel h2);
}
