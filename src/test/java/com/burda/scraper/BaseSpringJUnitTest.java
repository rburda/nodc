package com.burda.scraper;

import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={
		"classpath:/spring-context.xml",
		"classpath:/spring-context-dev.xml"})
@ActiveProfiles(profiles = "dev")
public abstract class BaseSpringJUnitTest
{

}
