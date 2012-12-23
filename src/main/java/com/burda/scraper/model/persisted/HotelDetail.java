package com.burda.scraper.model.persisted;

import java.io.Serializable;
import java.io.StringWriter;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.dynamodb.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBTable;
import com.burda.scraper.model.Amenity;
import com.burda.scraper.model.Photo;
import com.google.common.collect.Lists;

@DynamoDBTable(tableName = "nodc_hotel_content")
public class HotelDetail implements Serializable
{
	private static final long serialVersionUID = 1L;

	private static final Logger logger = LoggerFactory.getLogger(HotelDetail.class);
	
	private String name;
	private String address1;
	private String city;
	private String state;
	private String zip;
	private String description;
	private String areaDescription;	
	private String latitude;
	private String longitude;
	private float rating;
	private String mapUrl;
	private List<Photo> photos = Lists.newArrayList();
	private List<Amenity> amenities = Lists.newArrayList();
	
	@DynamoDBHashKey(attributeName = "hotel_name")
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	
	@DynamoDBAttribute(attributeName = "address")
	public String getAddress1()
	{
		return address1;
	}

	public void setAddress1(String address1)
	{
		this.address1 = address1;
	}

	@DynamoDBAttribute(attributeName = "city")
	public String getCity()
	{
		return city;
	}

	public void setCity(String city)
	{
		this.city = city;
	}

	@DynamoDBAttribute(attributeName = "state")
	public String getState()
	{
		return state;
	}

	public void setState(String state)
	{
		this.state = state;
	}

	@DynamoDBAttribute(attributeName = "zip")
	public String getZip()
	{
		return zip;
	}

	public void setZip(String zip)
	{
		this.zip = zip;
	}
	
	@DynamoDBAttribute(attributeName = "desc")
	public String getDescription()
	{
		return description;
	}
	
	public void setDescription(String desc)
	{
		this.description = desc;
	}
	
	@DynamoDBAttribute(attributeName = "lat")
	public String getLatitude()
	{
		return this.latitude;
	}
	
	public void setLatitude(String lat)
	{
		this.latitude = lat;
	}
	
	@DynamoDBAttribute(attributeName = "long")
	public String getLongitude()
	{
		return this.longitude;
	}
	
	public void setLongitude(String lng)
	{
		this.longitude = lng;
	}
	
	@DynamoDBIgnore
	public String getMapUrl()
	{
		return mapUrl;
	}
	
	@DynamoDBAttribute(attributeName = "area_desc")
	public String getAreaDescription()
	{
		return areaDescription;
	}

	public void setAreaDescription(String aDesc)
	{
		this.areaDescription = aDesc;
	}
	
	@DynamoDBAttribute(attributeName = "rating")
	public float getRating()
	{
		return this.rating;
	}
	
	public void setRating(float rating)
	{
		this.rating = rating;
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
	public List<Photo> getPhotos()
	{
		return photos;
	}
	
	@DynamoDBIgnore
	public void addPhoto(Photo p)
	{
		this.photos.add(p);
	}
	
	@JsonIgnore
	@DynamoDBAttribute(attributeName = "amenities_json")
	public String getAmenitiesJsonString()
	{
		String jsonString = null;
		StringWriter buffer = new StringWriter();
		ObjectMapper objMapper = new ObjectMapper();
		try
		{
			objMapper.writeValue(buffer, amenities);	
			jsonString = buffer.toString();
		}
		catch (Exception e)
		{
			logger.error("unable to create photo json string", e);
		}		
		return jsonString;
	}
	
	public void setAmenitiesJsonString(String jsonString)
	{
		ObjectMapper objMapper = new ObjectMapper();
		try
		{
			amenities = objMapper.readValue(jsonString, new TypeReference<List<Amenity>>(){});
		}
		catch (Exception e)
		{
			logger.error("unable to deserialize amenities", e);
		}
	}
	
	@DynamoDBIgnore
	public List<Amenity> getAmenities()
	{
		return amenities;
	}
	
	@DynamoDBIgnore
	public void addAmenity(Amenity a)
	{
		this.amenities.add(a);
	}
}