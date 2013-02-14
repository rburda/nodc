package com.nodc.scraper.dao;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

public class HotelDetailS3DAO
{
	private static final Logger logger = LoggerFactory.getLogger(HotelDetailS3DAO.class);
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
	
	public void saveHotelDetails(String hotelId, byte[] xmlAsBytes)
	{
		String fileKey = createFileKey(hotelId);
		ByteArrayInputStream is = new ByteArrayInputStream(xmlAsBytes);
		ObjectMetadata md = new ObjectMetadata();
		md.setContentLength(xmlAsBytes.length);
		
		s3.putObject(new PutObjectRequest(s3BucketName, fileKey, is, md));
	}
	
	public byte[] getHotelDetails(String hotelId)
	{
		S3Object file = s3.getObject(new GetObjectRequest(s3BucketName, createFileKey(hotelId)));
		byte[] contents = null;
		try
		{
			contents = readFile(file.getObjectContent());	
		}
		catch (Exception e)
		{
			logger.error("Unable to load file",e);
		}
		return contents;
		
	}
	
	private String createFileKey(String hotelId)
	{
		return hotelId + ".xml";
	}
	
	private byte[] readFile(InputStream is) throws Exception
	{
		StringBuffer buf = new StringBuffer();
		 BufferedReader reader = new BufferedReader(new InputStreamReader(is));
     while (reader.ready()) {
         buf.append(reader.readLine());
     }		
     return buf.toString().getBytes();
	}
}