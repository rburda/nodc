package com.burda.scraper.model;

import java.io.Serializable;

public class Amenity implements Serializable
{
	private static final long serialVersionUID = 1L;
	public String name;
	public String description;
	
	public String getName()
	{
		return name;
	}
	public String getDescription()
	{
		return description;
	}
	
}
