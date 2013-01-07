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
			BigDecimal lowestH1 = null;
			BigDecimal lowestH2 = null;
			for (RoomType rt: h1.getRoomTypes())
				if (lowestH1 == null || rt.getTotalPrice().compareTo(lowestH1) < 0 )
					lowestH1 = rt.getTotalPrice();				
			for (RoomType rt: h2.getRoomTypes())
				if (lowestH2 == null || rt.getTotalPrice().compareTo(lowestH2) < 0 )
					lowestH2 = rt.getTotalPrice();
			
			return lowestH1.compareTo(lowestH2);
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
