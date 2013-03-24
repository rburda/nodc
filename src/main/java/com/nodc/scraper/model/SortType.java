package com.nodc.scraper.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Comparator;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;

public enum SortType implements Comparator<Hotel>, Serializable
{
	DEFAULT_D 
	{
		@JsonIgnore
		@Override
		public int compare(Hotel h1, Hotel h2)
		{
			int h1Weight = (h1.getHotelDetails() == null ? 1000 : h1.getHotelDetails().getWeight());
			int h2Weight = (h2.getHotelDetails() == null ? 1000 : h2.getHotelDetails().getWeight());
			return  (h1Weight - h2Weight);
		}
	},
	DEFAULT_A 
	{
		@JsonIgnore
		@Override
		public int compare(Hotel h1, Hotel h2)
		{
			return DEFAULT_D.compare(h2, h1);
		}
	},
	PRICE_A 
	{
		@JsonIgnore
		@Override
		public int compare(Hotel h1, Hotel h2)
		{
			return h1.getLowestAvgRate().compareTo(h2.getLowestAvgRate());
		}
	},
	PRICE_D 
	{
		@JsonIgnore
		@Override
		public int compare(Hotel h1, Hotel h2)
		{
			return PRICE_A.compare(h2, h1);
		}
	},
	HOTEL_NAME_A 
	{
		@JsonIgnore
		@Override
		public int compare(Hotel h1, Hotel h2)
		{
			return h1.getName().compareTo(h2.getName());
		}
	}, 
	HOTEL_NAME_D 
	{
		@JsonIgnore
		@Override
		public int compare(Hotel h1, Hotel h2)
		{
			return HOTEL_NAME_A.compare(h2, h1);
		}
	},
	RATING_A 
	{
		@JsonIgnore
		@Override
		public int compare(Hotel h1, Hotel h2)
		{
			float h1Rating = (h1.getHotelDetails() == null ? 1 : h1.getHotelDetails().getRating());
			float h2Rating = (h2.getHotelDetails() == null ? 1 : h2.getHotelDetails().getRating());
			float result =  h1Rating - h2Rating;
			if (result < 0)
				return -1;
			if (result > 0)
				return 1;
			return 0;
		}
	},
	RATING_D
	{
		@JsonIgnore
		@Override
		public int compare(Hotel h1, Hotel h2)
		{
			return RATING_A.compare(h2, h1);
		}
	},
	AREA_A
	{
		@JsonIgnore
		@Override
		public int compare(Hotel h1, Hotel h2)
		{
			String area1 = (h1.getHotelDetails() == null ? "ZZZ" : StringUtils.defaultIfEmpty(h1.getHotelDetails().getAreaDescription(),"ZZZ"));
			String area2 = (h2.getHotelDetails() == null ? "ZZZ" : StringUtils.defaultIfEmpty(h2.getHotelDetails().getAreaDescription(),"ZZZ"));
			return area1.compareTo(area2);
		}
	},
	AREA_D
	{
		@JsonIgnore
		@Override
		public int compare(Hotel h1, Hotel h2)
		{
			return AREA_A.compare(h2, h1);
		}
	};

	@JsonIgnore
	@Override
	public abstract int compare(Hotel h1, Hotel h2);
}
