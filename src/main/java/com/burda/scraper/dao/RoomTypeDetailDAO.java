package com.burda.scraper.dao;

import java.util.List;

import com.amazonaws.services.dynamodb.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodb.model.AttributeValue;
import com.burda.scraper.model.persisted.RoomTypeDetail;
import com.google.code.ssm.api.ParameterValueKeyProvider;
import com.google.code.ssm.api.ReadThroughSingleCache;

public class RoomTypeDetailDAO extends AbstractDynamoDBDAO<RoomTypeDetail>
{
	@ReadThroughSingleCache(namespace = "RoomTypeDetail", expiration = 3600)
	public List<RoomTypeDetail> getHotelDetail(@ParameterValueKeyProvider(order=1) HotelDetailCacheKey ck)
	{
		DynamoDBQueryExpression queryExpression = 
				new DynamoDBQueryExpression(new AttributeValue().withS(ck.getHotelName()));

		return getDynamoMapper().query(RoomTypeDetail.class, queryExpression);
	}
	
	
}