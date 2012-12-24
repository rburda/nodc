package com.burda.scraper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Purpose of this class is to allow us to programmatically look for a custom
 * parameter to determine the spring profile to use when initializing beans.
 * This is needed in the AWS ElasticBeanStalk world as the parameter names 
 * available to us are PARAM1, PARAM2, PARAM3.... etc... instead of the typical
 * name that spring looks for 'spring.profiles.active'
 * 
 * This class is referenced as a contextInitializerClass in the web.xml
 * @author rburda
 *
 */
public class ContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext>
{
	private static Logger logger = LoggerFactory.getLogger(ContextInitializer.class);
	@Override
	public void initialize(ConfigurableApplicationContext cac)
	{
		String profileParam = System.getProperty("PARAM1");
		String profile = "dev";
		if (profileParam != null && profileParam.equals("live"))
			profile = "live";
		
		logger.info("setting profile to: " + profile);
		cac.getEnvironment().setActiveProfiles(profile);
	}
}