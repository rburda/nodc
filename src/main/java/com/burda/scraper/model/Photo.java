package com.burda.scraper.model;

import java.io.Serializable;

public class Photo implements Serializable
{
	private static final long serialVersionUID = 1L;
	public String url;
	
	public String getUrl()
	{
		return url;
	}
	
	public boolean equals(Object o)
	{
		if (o == null)
			return false;
		
		if (!(o instanceof Photo))
			return false;
		
		Photo other = (Photo)o;
		return other.url.equals(url);
	}
	
	public int hashCode()
	{
		return url.hashCode();
	}
}