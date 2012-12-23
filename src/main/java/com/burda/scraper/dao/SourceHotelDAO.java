package com.burda.scraper.dao;

import com.burda.scraper.model.persisted.InventorySource;
import com.burda.scraper.model.persisted.SourceHotel;
import com.google.code.ssm.api.ParameterValueKeyProvider;
import com.google.code.ssm.api.ReadThroughSingleCache;

public class SourceHotelDAO extends AbstractDynamoDBDAO<SourceHotel>
{	
	@ReadThroughSingleCache(namespace = "SourceHotel", expiration = 3600)
	public SourceHotel getByHotelId(
		@ParameterValueKeyProvider(order=1) String hotelId, @ParameterValueKeyProvider(order=2) InventorySource is)
	{
		/*
		DynamoDBQueryExpression queryExpression = 
				new DynamoDBQueryExpression(
						new AttributeValue().withS(hotelId)).withRangeKeyCondition(
								new Condition().withAttributeValueList(
										new AttributeValue().withS(is.name())));
		*/
		return getDynamoMapper().load(SourceHotel.class,  hotelId, is.name());
	}
}