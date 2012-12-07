package com.burda.scraper.inventory;

import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.burda.scraper.model.SearchResult;

public class FrenchQuarterGuideInventorySource implements InventorySource
{
	private static final Logger logger = LoggerFactory.getLogger(FrenchQuarterGuideInventorySource.class);	
	
	@Override
	public SearchResult getResults()
	{
		// TODO Auto-generated method stub
		return null;
	}

	private HttpResponse queryHotelsViaHttpClient()
	{
		URIBuilder builder = new URIBuilder();
		builder
				.setScheme("http")
				.setHost("www.neworleans.com")
				.setPath("/mytrip/app/")
				.addParameter("id88_hf_0", "")
				.addParameter("productType", "HOTEL")
				.addParameter("promoId", "")
				.addParameter("mo", "")
				.addParameter("pdp:physicalDestination", "982")
				.addParameter("departureDate", "11/30/2012")
				.addParameter("returnDate", "12/02/2012")
				.addParameter("numRooms", "1")
				.addParameter("r1:0:ro:na", "2")
				.addParameter("rl:0:ro:ob:nc", "0")
				.addParameter("rl:1:ro:na", "1")
				.addParameter("rl:1:ro:ob:nc", "0")
				.addParameter("rl:2:ro:na", "1")
				.addParameter("rl:2:ro:ob:nc", "0")
				.addParameter("rl:3:ro:na", "1")
				.addParameter("rl:3:ro:ob:nc", "0")
				.addParameter("a:0:b:c:0:d:e", "")
				.addParameter("a:0:b:c:1:d:e", "")
				.addParameter("a:0:b:c:2:d:e", "")
				.addParameter("a:1:b:c:0:d:e", "")
				.addParameter("a:1:b:c:2:d:e", "")
				.addParameter("a:2:b:c:0:d:e", "")
				.addParameter("a:2:b:c:1:d:e", "")
				.addParameter("a:2:b:c:2:d:e", "")
				.addParameter("a:3:b:c:0:d:e", "")
				.addParameter("a:3:b:c:1:d:e", "")
				.addParameter("a:3:b:c:2:d:e", "")
				.addParameter("preferredProductId", "")
				.addParameter("wicket:bookmarkablePage",
						":com.vegas.athena.components.browse.hotel.HotelBrowsePage");					
		HttpResponse response = null;
		try
		{
			URI uri = builder.build();
			HttpGet httpget = new HttpGet(uri);
			logger.error("uri == " + httpget.getURI());

			response = new DefaultHttpClient().execute(httpget);
		} 
		catch (Exception e)
		{
			logger.error("unable to retrieve query response", e);
		}
		return response;
	}	
	
}
