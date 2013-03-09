package com.nodc.scraper.dao;

import java.util.List;

import com.amazonaws.services.dynamodb.AmazonDynamoDB;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodb.model.AttributeValue;

public abstract class AbstractDynamoDBDAO<T>
{
	private final DynamoDBMapper mapper;
	
	protected AbstractDynamoDBDAO(AmazonDynamoDB client)
	{
		this.mapper = new DynamoDBMapper(client);
	}
	
	public void save (T t)
	{
		getDynamoMapper().save(t);
	}
	
	public void save(List<T> tList)
	{
		getDynamoMapper().batchSave(tList);
	}
	
	public void delete(T t) {
		getDynamoMapper().delete(t);
	}
	
	public void delete(List<T> t) {
		getDynamoMapper().batchDelete(t);
	}
	
	
	protected final DynamoDBMapper getDynamoMapper()
	{
		return mapper;
	}
}
