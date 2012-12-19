package com.burda.scraper.dao;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

public class HotelDetailDAO
{
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
		String fileKey = hotelId + ".xml";
		ByteArrayInputStream is = new ByteArrayInputStream(xmlAsBytes);
		ObjectMetadata md = new ObjectMetadata();
		md.setContentLength(xmlAsBytes.length);
		
		s3.putObject(new PutObjectRequest(s3BucketName, fileKey, is, md));
	}
	
	public byte[] getHotelDetails(String hotelId)
	{
		return null;
		//S3Object obj = s3.getObject(new GetObjectRequest(s3BucketName, fileKey));
		//InputStreamReader isr = new InputStreamReader(obj.getObjectContent());
		
		//os.write(obj.getObjectContent().)
	}
}
