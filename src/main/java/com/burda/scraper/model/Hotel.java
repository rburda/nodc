package com.burda.scraper.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;


public class Hotel
{
	public String source;
	public String internalName; //used for filtering etc...
	public String name;
	public String description;
	public String mapUrl;
	public String photosUrl;
	public String areaDescription;
	public String moreInfoUrl;
	public List<RoomType> roomTypes = new ArrayList<RoomType>();
	
	public String getSource()
	{
		return source;
	}
	public String getInternalName()
	{
		return internalName;
	}
	public String getName()
	{
		return name;
	}
	public String getDescription()
	{
		return description;
	}
	public String getMapUrl()
	{
		return mapUrl;
	}
	public String getPhotosUrl()
	{
		return photosUrl;
	}
	public String getAreaDescription()
	{
		return areaDescription;
	}
	public String getMoreInfoUrl()
	{
		return moreInfoUrl;
	}
	public List<RoomType> getRoomTypes()
	{
		return roomTypes;
	}
	
	public String toString()
	{
		return ToStringBuilder.reflectionToString(this);
	}
}