package com.burda.scraper.cache;

import java.io.ByteArrayInputStream;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

public class FrenchQuarterGuideCacheLoader
{
	private Logger logger = LoggerFactory.getLogger(FrenchQuarterGuideCacheLoader.class);
	
	private AmazonS3Client s3 = null;
	private String s3BucketName = null;
	
	public void setAwsS3Client(AmazonS3Client awsClient)
	{
		this.s3 = awsClient;
	}
	
	public void setS3BucketName(String bucketName)
	{
		this.s3BucketName = bucketName;
	}
	
	public void loadCache() throws Exception
	{
		boolean hasMorePages = true;
		int page = 1;
		while (hasMorePages)
		{
			HttpResponse hotelSummaryResponse = queryGetHotelResults(page);
			byte[] xml = EntityUtils.toByteArray(hotelSummaryResponse.getEntity());
			Document xmlSummaryResults = Jsoup.parse(new String(xml), "", Parser.xmlParser());

			int totalNumPages = Integer.valueOf(xmlSummaryResults.select("hotel_data").select("total_pages").first().ownText());
			hasMorePages = (page++ < totalNumPages);
			

			for (Element hotelSummaryEl: xmlSummaryResults.select("hotel"))
			{
				String hotelId = hotelSummaryEl.select("hotel_id").first().ownText();
				HttpResponse hotelDetailResponse = queryGetHotelData(hotelId);
				
				String fileKey = hotelId + ".xml";
				byte[] xmlAsBytes = EntityUtils.toByteArray(hotelDetailResponse.getEntity());
				ByteArrayInputStream is = new ByteArrayInputStream(xmlAsBytes);
				ObjectMetadata md = new ObjectMetadata();
				md.setContentLength(xmlAsBytes.length);
				
				s3.putObject(new PutObjectRequest(s3BucketName, fileKey, is, md));				
			}
			
		}
		
	}
	
	/**
	 * gets detail data on a specific hotel
	 * @return
	 */
	private HttpResponse queryGetHotelData(String hotelId)
	{
		URIBuilder builder = new URIBuilder();
		builder
				.setScheme("https")
				.setHost("secure.rezserver.com")
				.setPath("/api/hotel/getHotelData")
				.addParameter("api_key", "5f871629935ff113b876b4bcb1ca70e4")
				.addParameter("refid", "5057")
				.addParameter("hotel_id", hotelId)
				.addParameter("check_in", new DateTime().plusDays(60).toString(DateTimeFormat.forPattern("MM/dd/yyyy")))
				.addParameter("check_out", new DateTime().plusDays(61).toString(DateTimeFormat.forPattern("MM/dd/yyyy")))
				.addParameter("rooms", "1");
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
	
	/**
	 * Returns xml that lists a summary level detail of all hotels
	 * @return
	 */
	private HttpResponse queryGetHotelResults(int page)
	{
		URIBuilder builder = new URIBuilder();
		builder
				.setScheme("https")
				.setHost("secure.rezserver.com")
				.setPath("/api/hotel/getResults")
				.addParameter("api_key", "5f871629935ff113b876b4bcb1ca70e4")
				.addParameter("refid", "5057")
				.addParameter("city_id", "3000008434")
				.addParameter("check_in", new DateTime().plusDays(60).toString(DateTimeFormat.forPattern("MM/dd/yyyy")))
				.addParameter("check_out", new DateTime().plusDays(61).toString(DateTimeFormat.forPattern("MM/dd/yyyy")))
				.addParameter("page", String.valueOf(page));
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
