package com.nodc.scraper.model.persisted;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.dynamodb.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBTable;
import com.nodc.scraper.model.DailyRate;
import com.nodc.scraper.model.Photo;
import com.google.common.collect.Lists;

@DynamoDBTable(tableName = "nodc_hotel_room_type_content")
public class RoomTypeDetail
{
	private static final Logger logger = LoggerFactory.getLogger(RoomTypeDetail.class);
	
	public static final String createRoomTypeDetailHotelName(String hotelName, InventorySource is)
	{
		return hotelName+"_"+is;
	}
	
	private String hotelName;
	private String name;
	private String description;
	private String details;
	private String features;
	private List<Photo> photos = Lists.newArrayList();
	private List<DailyRate> dailyRates = Lists.newArrayList();
	private String bookItUrl;
	private BigDecimal avgNightlyRate;
	private BigDecimal avgNightlyOriginalRate;
	private boolean isPromoRate;
	
	@DynamoDBHashKey(attributeName = "hotel_name")
	public String getHotelName()
	{
		return hotelName;
	}
	
	public void setHotelName(String hn)
	{
		this.hotelName = hn;
	}
	
	@DynamoDBRangeKey(attributeName = "room_type_name")
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	
	@DynamoDBAttribute(attributeName = "description")
	public String getDescription()
	{
		return description;
	}
	public void setDescription(String description)
	{
		this.description = description;
	}
	
	@DynamoDBAttribute(attributeName = "details")
	public String getDetails()
	{
		return details;
	}
	public void setDetails(String details)
	{
		this.details = details;
	}
	
	@DynamoDBAttribute(attributeName = "features")
	public String getFeatures()
	{
		return features;
	}
	public void setFeatures(String features)
	{
		this.features = features;
	}
	
	@DynamoDBIgnore
	public List<DailyRate> getDailyRates()
	{
		return dailyRates;
	}
	
	@DynamoDBIgnore
	public void setDailyRates(List<DailyRate> rates)
	{
		this.dailyRates = rates;
	}
	
	@DynamoDBIgnore
	public List<Photo> getPhotos()
	{
		return photos;
	}
	public void setPhotos(List<Photo> photos)
	{
		this.photos = photos;
	}
	
	@JsonIgnore
	@DynamoDBAttribute(attributeName = "photos_json")
	public String getPhotosJsonString()
	{
		String jsonString = null;
		StringWriter buffer = new StringWriter();
		ObjectMapper objMapper = new ObjectMapper();
		try
		{
			objMapper.writeValue(buffer, photos);	
			jsonString = buffer.toString();
		}
		catch (Exception e)
		{
			logger.error("unable to create photo json string", e);
		}		
		return jsonString;
	}

	public void setPhotosJsonString(String jsonString)
	{
		ObjectMapper objMapper = new ObjectMapper();
		try
		{
			photos = objMapper.readValue(jsonString, new TypeReference<List<Photo>>(){});
		}
		catch (Exception e)
		{
			logger.error("unable to deserialize photos", e);
		}
	}	
	
	@DynamoDBIgnore
	public void addPhoto(Photo p)
	{
		if (!this.photos.contains(p))
			this.photos.add(p);
	}

	@Override
	public boolean equals(Object o)
	{
		if (o == null)
			return false;
		
		if (!(o instanceof RoomTypeDetail))
			return false;
		
		RoomTypeDetail other = (RoomTypeDetail)o;
		boolean equals = false;
		if (other.getHotelName().equals(getHotelName()))
				if (other.getName().equals(getName()))
					equals = true;
		
		return equals;
	}
	
	@DynamoDBIgnore
	public String getBookItUrl()
	{
		return bookItUrl;
	}
	
	@DynamoDBIgnore
	public void setBookItUrl(String biu)
	{
		this.bookItUrl = biu;
	}

	@DynamoDBIgnore
	public BigDecimal getAvgNightlyRate()
	{
		return avgNightlyRate;
	}

	@DynamoDBIgnore
	public void setAvgNightlyRate(BigDecimal avgNightlyRate)
	{
		this.avgNightlyRate = avgNightlyRate;
	}
	
	@DynamoDBIgnore
	public BigDecimal getAvgNightlyOriginalRate()
	{
		return avgNightlyOriginalRate;
	}
	
	@DynamoDBIgnore
	public void setAvgNightlyOriginalRate(BigDecimal avgNightlyOriginalRate)
	{
		this.avgNightlyOriginalRate = avgNightlyOriginalRate;
	}
	
	@DynamoDBIgnore
	public boolean isPromoRate()
	{
		return isPromoRate;
	}
	
	@DynamoDBIgnore
	public void setPromoRate(boolean isPromoRate)
	{
		this.isPromoRate = isPromoRate;
	}

	@Override
	public int hashCode()
	{
		return getHotelName().hashCode();
	}
}