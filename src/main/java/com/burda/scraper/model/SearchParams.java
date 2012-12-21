package com.burda.scraper.model;

import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class SearchParams
{
	public static SearchParams oneRoomOneAdult()
	{
		SearchParams sp = new SearchParams();
		sp.numAdults1 = 1;
		sp.numRooms = 1;
		sp.checkInDate = new LocalDate().plusDays(1);
		sp.checkOutDate = new LocalDate().plusDays(3);
		
		return sp;
	}
	
	private DateTimeFormatter CHECK_IN_OUT_FORMAT = DateTimeFormat.forPattern("MM/dd/yyyy");
	
	private LocalDate checkInDate;	
	private LocalDate checkOutDate;
	private int numRooms = 0;
	private int numAdults1 = 0;
	private int numChildren1 = 0;
	private int numAdults2 = 0;
	private int numChildren2 = 0;
	private int numAdults3 = 0;
	private int numChildren3 = 0;
	private int numAdults4 = 0;
	private int numChildren4 = 0;
	private int room1ChildAge1 = 0;
	private int room1ChildAge2 = 0;
	private int room1ChildAge3 = 0;
	private int room2ChildAge1 = 0;
	private int room2ChildAge2 = 0;
	private int room2ChildAge3 = 0;
	private int room3ChildAge1 = 0;
	private int room3ChildAge2 = 0;
	private int room3ChildAge3 = 0;
	private int room4ChildAge1 = 0;
	private int room4ChildAge2 = 0;
	private int room4ChildAge3 = 0;
	
	private SearchParams(){}
	
	public SearchParams(Map<String, String> requestParams)
	{
		checkInDate = LocalDate.parse(requestParams.get("departureDate"), CHECK_IN_OUT_FORMAT);
		checkOutDate = LocalDate.parse(requestParams.get("returnDate"), CHECK_IN_OUT_FORMAT);
		numRooms = defZero(requestParams.get("numRooms"));
		numAdults1 = defZero(requestParams.get("rl:0:ro:na"));
		numChildren1 = defZero(requestParams.get("rl:0:ro:ob:nc"));
		numAdults2 = defZero(requestParams.get("rl:1:ro:na"));
		numChildren2 = defZero(requestParams.get("rl:1:ro:ob:nc"));
		numAdults3 = defZero(requestParams.get("rl:2:ro:na"));
		numChildren3 = defZero(requestParams.get("rl:2:ro:ob:nc"));
		numAdults4 = defZero(requestParams.get("rl:3:ro:na"));
		numChildren4 = defZero(requestParams.get("rl:3:ro:ob:nc"));
		room1ChildAge1 = defZero(requestParams.get("a:0:b:c:0:d:e"));
		room1ChildAge2 = defZero(requestParams.get("a:0:b:c:1:d:e"));
		room1ChildAge3 = defZero(requestParams.get("a:0:b:c:2:d:e"));
		room2ChildAge1 = defZero(requestParams.get("a:1:b:c:0:d:e"));
		room2ChildAge2 = defZero(requestParams.get("a:1:b:c:1:d:e"));
		room2ChildAge3 = defZero(requestParams.get("a:1:b:c:2:d:e"));
		room3ChildAge1 = defZero(requestParams.get("a:2:b:c:0:d:e"));
		room3ChildAge2 = defZero(requestParams.get("a:2:b:c:1:d:e"));
		room3ChildAge3 = defZero(requestParams.get("a:2:b:c:2:d:e"));
		room4ChildAge1 = defZero(requestParams.get("a:3:b:c:0:d:e"));
		room4ChildAge2 = defZero(requestParams.get("a:3:b:c:1:d:e"));
		room4ChildAge3 = defZero(requestParams.get("a:3:b:c:2:d:e"));
	}
	
	public int getNumRooms()
	{
		return numRooms;
	}
	public int getNumAdults1()
	{
		return numAdults1;
	}
	public int getNumChildren1()
	{
		return numChildren1;
	}
	public int getNumAdults2()
	{
		return numAdults2;
	}
	public int getNumChildren2()
	{
		return numChildren2;
	}
	public int getNumAdults3()
	{
		return numAdults3;
	}
	public int getNumChildren3()
	{
		return numChildren3;
	}
	public int getNumAdults4()
	{
		return numAdults4;
	}
	public int getNumChildren4()
	{
		return numChildren4;
	}
	public int getRoom1ChildAge1()
	{
		return room1ChildAge1;
	}
	public int getRoom1ChildAge2()
	{
		return room1ChildAge2;
	}
	public int getRoom1ChildAge3()
	{
		return room1ChildAge3;
	}
	public int getRoom2ChildAge1()
	{
		return room2ChildAge1;
	}
	public int getRoom2ChildAge2()
	{
		return room2ChildAge2;
	}
	public int getRoom2ChildAge3()
	{
		return room2ChildAge3;
	}
	public int getRoom3ChildAge1()
	{
		return room3ChildAge1;
	}
	public int getRoom3ChildAge2()
	{
		return room3ChildAge2;
	}
	public int getRoom3ChildAge3()
	{
		return room3ChildAge3;
	}
	public int getRoom4ChildAge1()
	{
		return room4ChildAge1;
	}
	public int getRoom4ChildAge2()
	{
		return room4ChildAge2;
	}
	public int getRoom4ChildAge3()
	{
		return room4ChildAge3;
	}	
	public LocalDate getCheckInDate()
	{
		return checkInDate;
	}
	public LocalDate getCheckOutDate()
	{
		return checkOutDate;
	}

	private int defZero(String s)
	{
		int returnVal;
		if (s == null || s.length() == 0)
			returnVal = 0;
		else
			returnVal = Integer.valueOf(s);
		return returnVal;
	}
	
	public String toString()
	{
		return ToStringBuilder.reflectionToString(this);
	}
}