package com.nodc.scraper.model.persisted;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.amazonaws.services.dynamodb.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName="nodc_cache_update")
public class CacheState
{
	private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormat.forPattern("MM/dd/yyyy HH:mm:ss");
	
	private String cacheName;
	private DateTime lastUpdated;
	
	@DynamoDBHashKey(attributeName="cache_name")
	public String getCacheName()
	{
		return cacheName;
	}
	public void setCacheName(String cacheName)
	{
		this.cacheName = cacheName;
	}
	
	@DynamoDBAttribute(attributeName="last_update_datetime")
	public String getLastUpdateDateTimeString()
	{
		String lastUpdatedString = null;
		if (lastUpdated != null)
			lastUpdatedString = DATE_TIME_FORMAT.print(lastUpdated);
		return lastUpdatedString;
	}
	public void setLastUpdateDateTimeString(String lastUpdatedString)
	{
		if (lastUpdatedString != null)
			lastUpdated = DATE_TIME_FORMAT.parseDateTime(lastUpdatedString);
	}
	
	@DynamoDBIgnore
	public DateTime getLastUpdateDateTime()
	{
		return lastUpdated;
	}
	
	@DynamoDBIgnore
	public void markCacheAsUpdated()
	{
		this.lastUpdated = new DateTime();
	}
}
