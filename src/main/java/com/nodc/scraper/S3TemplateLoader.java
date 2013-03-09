package com.nodc.scraper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

import freemarker.cache.TemplateLoader;

@Component(value="s3TemplateLoader")
public class S3TemplateLoader implements TemplateLoader
{
	private static final Logger logger = LoggerFactory.getLogger(S3TemplateLoader.class);
	private static final String FTL_BUCKET_NAME = "nodc-prod";
	
	private final AmazonS3 client;
	
	@Autowired
	public S3TemplateLoader(AmazonS3 client)
	{
		this.client = client;
	}
	
	@Override
	public Object findTemplateSource(String name) throws IOException
	{
		logger.error("loading template (orig): " + name);
		name = StringUtils.remove(name, "_en_US");
		logger.error("loading template (mod): " + name);
		GetObjectRequest gor = new GetObjectRequest(FTL_BUCKET_NAME, name);
		Object template = null;
		try
		{
			template = client.getObject(gor);
		}
		catch (Throwable t)
		{
			logger.warn("unable to load template: " + name + t.getMessage());
		}
		return template;
	}

	@Override
	public long getLastModified(Object templateSource)
	{
		S3Object s3Object = (S3Object)templateSource;
		return s3Object.getObjectMetadata().getLastModified().getTime();
	}

	@Override
	public Reader getReader(Object templateSource, String encoding) throws IOException
	{
		S3Object s3Object = (S3Object)templateSource;
		return new BufferedReader(new InputStreamReader(s3Object.getObjectContent()));
	}

	@Override
	public void closeTemplateSource(Object templateSource) throws IOException
	{
		S3Object s3Object = (S3Object)templateSource;
		s3Object.getObjectContent().close();
	}
}