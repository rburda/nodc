package com.burda.scraper.dao;

import com.burda.scraper.model.persisted.InventorySource;
import com.burda.scraper.model.persisted.SourceHotel;

public class SourceHotelDAO extends AbstractDynamoDBDAO<SourceHotel>
{	
	public SourceHotel getByHotelId(String hotelId, InventorySource is)
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
