package com.nodc.scraper.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.nodc.scraper.model.persisted.HotelDetail;
import com.nodc.scraper.model.persisted.SourceHotel;

public class Hotel
{
	private SourceHotel source;
	private HotelDetail details = new HotelDetail();
	private String name;
	private List<RoomType> roomTypes = new ArrayList<RoomType>();
	
	public SourceHotel getSource()
	{
		return source;
	}
	
	public void setSource(SourceHotel sh)
	{
		this.source = sh;
	}

	public String getName()
	{
		return name;
	}
	
	public void setName(String n)
	{
		this.name = n;
	}
	
	public HotelDetail getHotelDetails()
	{
		return details;
	}
	
	public void setHotelDetails(HotelDetail hd)
	{
		this.details = hd;
	}
	
	public List<RoomType> getRoomTypes()
	{
		return roomTypes;
	}
	
	public BigDecimal getLowestAvgRate()
	{
		BigDecimal lowest = null;
		for (RoomType rt: getRoomTypes())
		{
			if (lowest == null || rt.getAvgNightlyRate().compareTo(lowest) < 0)
				lowest = rt.getAvgNightlyRate();
		}
		return (lowest == null ? BigDecimal.ZERO: lowest);
	}
	
	public void addRoomType(RoomType rt)
	{
		this.roomTypes.add(rt);
	}
	
	public String toString()
	{
		return name;
		//return ToStringBuilder.reflectionToString(this);
	}
}