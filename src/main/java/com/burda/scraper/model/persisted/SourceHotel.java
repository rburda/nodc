package com.burda.scraper.model.persisted;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.amazonaws.services.dynamodb.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "nodc_hotel_source")
public class SourceHotel implements Serializable
{
	private static final long serialVersionUID = 1L;
	private String hotelName;
	private InventorySource invSource;
	private String externalHotelId;
	
	@DynamoDBAttribute(attributeName = "hotel_name")	
	public String getHotelName()
	{
		return hotelName;
	}
	public void setHotelName(String hotelName)
	{
		this.hotelName = hotelName;
	}

	@DynamoDBRangeKey(attributeName = "inv_source")
	public String getInventorySourceString()
	{
		return this.invSource.name();
	}
	public void setInventorySourceString(String invSourceString)
	{
		this.invSource = InventorySource.valueOf(invSourceString);
	}	
	
	@DynamoDBIgnore
	public InventorySource getInvSource()
	{
		return invSource;
	}
	public void setInvSource(InventorySource invSource)
	{
		this.invSource = invSource;
	}
	
	@DynamoDBHashKey(attributeName = "external_hotel_id")	
	public String getExternalHotelId()
	{
		return externalHotelId;
	}
	public void setExternalHotelId(String externalHotelId)
	{
		this.externalHotelId = externalHotelId;
	}
	
}
