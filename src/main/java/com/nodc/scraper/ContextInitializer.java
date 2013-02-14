package com.nodc.scraper;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.ObjectArrays;

/**
 * Purpose of this class is to allow us to programmatically look for a custom
 * parameter to determine the spring profile to use when initializing beans.
 * This is needed in the AWS ElasticBeanStalk world as the parameter names 
 * available to us are PARAM1, PARAM2, PARAM3.... etc... instead of the typical
 * name that spring looks for 'spring.profiles.active'
 * 
 * This class is referenced as a contextInitializerClass in the web.xml
 * @author rnodc
 *
 */
public class ContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext>
{
	private static Logger logger = LoggerFactory.getLogger(ContextInitializer.class);
	@Override
	public void initialize(ConfigurableApplicationContext cac)
	{
		String profileParam = System.getProperty("PARAM1");
		if (!StringUtils.isEmpty(profileParam))
		{
			List<String> profiles = Lists.newArrayList(Splitter.on(",").split(profileParam));
			String[] profileArray = profiles.toArray(ObjectArrays.newArray(String.class, profiles.size()));
			cac.getEnvironment().setActiveProfiles(profileArray);
			logger.info("setting profile to: " + profiles);
		}
		else
		{
			cac.getEnvironment().setActiveProfiles("dev");
			logger.info("setting profile to: dev");
		}
		

		
		

	}
}