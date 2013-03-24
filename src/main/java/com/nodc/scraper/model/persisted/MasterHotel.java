package com.nodc.scraper.model.persisted;

import java.util.Comparator;

import com.amazonaws.services.dynamodb.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "nodc_hotel")
public class MasterHotel
{
	public static final class EditableMasterHotel extends MasterHotel
	{
		private String newHotelName;
		private int newWeight;
		
		public EditableMasterHotel()
		{
			
		}
		
		@DynamoDBIgnore
		public int getNewWeight()
		{
			return newWeight;
		}
		
		public void setNewWeight(int w)
		{
			this.newWeight = w;
		}
		
		@DynamoDBIgnore
		public String getNewHotelName()
		{
			return newHotelName;
		}
		
		public void setNewHotelName(String nhn)
		{
			this.newHotelName = nhn;
		}
	}
	
	public static final Comparator<MasterHotel> BY_NAME = new Comparator<MasterHotel>(){

		@Override
		public int compare(MasterHotel m1, MasterHotel m2)
		{
			return m1.getHotelName().compareTo(m2.getHotelName());
		}};
		
	public static final Comparator<MasterHotel> BY_WEIGHT = new Comparator<MasterHotel>() {
		@Override
		public int compare(MasterHotel m1, MasterHotel m2)
		{
			return m1.getWeight() - m2.getWeight();
		}
	};
	
	private String hotelName;
	private int weight;
	private InventorySource favoredInvSource;
	private String uuid;
	
	public MasterHotel(){}
	
	@DynamoDBHashKey(attributeName = "hotel_name")
	public String getHotelName()
	{
		return hotelName;
	}
	
	public void setHotelName(String hn)
	{
		this.hotelName = hn;
	}
	
	@DynamoDBAttribute(attributeName = "weight")
	public int getWeight()
	{
		return weight;
	}
	
	public void setWeight(int w)
	{
		this.weight = w;
	}
	
	@DynamoDBIgnore
	public InventorySource getFavoredInventorySource()
	{
		return favoredInvSource;
	}
	
	public void setFavoredInventorySource(InventorySource is)
	{
		this.favoredInvSource = is;
	}
	
	@DynamoDBAttribute(attributeName = "default_source")
	public String getFavoredInventorySourceString()
	{
		return favoredInvSource.name();
	}
	
	public void setFavoredInventorySourceString(String is)
	{
		this.favoredInvSource = InventorySource.valueOf(is);
	}
	
	@DynamoDBAttribute(attributeName = "uuid")
	public String getUuid()
	{
		return uuid;
	}
	
	public void setUuid(String uuid)
	{
		this.uuid = uuid;
	}
}