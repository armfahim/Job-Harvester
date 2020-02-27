package io.naztech.jobharvestar.scraper.linkedin;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Horizon Robotics <br> 
 * URL: https://www.linkedin.com/jobs/search?keywords=Horizon%20Robotics&locationId=us%3A0&pageNum=0&position=1&f_C=7973570
 * 
 * @author Tanbirul Hashan
 * @since 2019-03-13
 */
@Service
public class HorizonRobotics extends AbstractLinkedinJobs {

	private static final String SITE = ShortName.HORIZON_ROBOTICS;

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return null;
	}
}
