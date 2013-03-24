package com.nodc.scraper.inventory;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.amazonaws.services.dynamodb.AmazonDynamoDB;
import com.amazonaws.services.dynamodb.model.AttributeValue;
import com.amazonaws.services.dynamodb.model.PutItemRequest;
import com.google.common.collect.Maps;
import com.nodc.scraper.dao.CacheStateDAO;
import com.nodc.scraper.model.persisted.CacheState;

public class ContentEditor
{
	private String tableName;
	private String name;
	private Map<String, String> attributes = Maps.newTreeMap();
	private Map<String, Boolean> overrideStatus = Maps.newHashMap();
	
	public ContentEditor() {}
	
	public ContentEditor(String tName, String name)
	{
		this.tableName = tName;
		this.name = name;
	}
	
	public void addAttributes(Map<String, AttributeValue> attrs)
	{
		if (attrs != null && !attrs.isEmpty())
		{
			for (String s: attrs.keySet())
				this.attributes.put(s,  (attrs.get(s).getS() == null ? attrs.get(s).getN() : attrs.get(s).getS()));			
		}
	}
	
	public Map<String, String> getAttributes()
	{
		return attributes;
	}
	
	public void addOverrideStatus(Map<String, Boolean> oStatus)
	{
		this.overrideStatus = oStatus;
	}
	
	public Map<String, Boolean> getOverrideStatus()
	{
		return overrideStatus;
	}
	
	public Boolean getOverrideStatus(String key)
	{
		Boolean value = overrideStatus.get(key);
		if (value == null)
			value = Boolean.FALSE;
		return value;
	}
	
	public String getTableName()
	{
		return tableName;
	}
	
	public void setTableName(String tn)
	{
		this.tableName = tn;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String n)
	{
		this.name = n;
	}
	
	void save(AmazonDynamoDB client, CacheStateDAO csDAO)
	{
		Map<String, AttributeValue> persistValueMap = Maps.newHashMap();
		for (String key: attributes.keySet())
		{
			String val = StringUtils.defaultIfEmpty(attributes.get(key), "").trim();
			AttributeValue av = new AttributeValue();
			if (key.equals("rating"))
				av.setN(val);
			else
				av.setS(val);
			persistValueMap.put(key, av);
		}

		Map<String, AttributeValue> persistOverrideMap = Maps.newHashMap();
		for (String key: overrideStatus.keySet())
		{
			persistOverrideMap.put(key, new AttributeValue(overrideStatus.get(key).toString()));
		}
		persistOverrideMap.put(getHashKey(), new AttributeValue(name));

		client.putItem( 
				new PutItemRequest().withTableName(tableName).withItem(persistValueMap));
		client.putItem( 
				new PutItemRequest().withTableName(tableName+"_override").withItem(persistOverrideMap));
		
		CacheState cs = csDAO.getCacheState(tableName);
		cs.markCacheAsUpdated();
		csDAO.save(cs);
	}
	
	private String getHashKey()
	{
		String key = null;
		for (String attrKey: attributes.keySet())
		{
			if (attributes.get(attrKey).equals(name))
				key = attrKey;
		}
		return key;
	}
}