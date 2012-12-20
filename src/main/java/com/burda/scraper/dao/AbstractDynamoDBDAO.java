package com.burda.scraper.dao;

import java.util.List;

import com.amazonaws.services.dynamodb.AmazonDynamoDB;
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBMapper;

public class AbstractDynamoDBDAO<T>
{
	private DynamoDBMapper mapper = null;

	
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
	
	public final void setDynamoDBClient(AmazonDynamoDB dbClient)
	{
		mapper = new DynamoDBMapper(dbClient);
	}	
}
