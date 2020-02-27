package io.naztech.jobharvestar.scraper.linkedin;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * CloverHealth <br> 
 * URL: https://www.linkedin.com/jobs/search/?locationId=OTHERS.worldwide&f_C=4836709&keywords=&pageNum=0&position=1
 * 
 * @author Tanbirul Hashan
 * @since 2019-03-11
 */
@Service
public class CloverHealth extends AbstractLinkedinJobs {

	private static final String SITE = ShortName.CLOVER_HEALTH;

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return null;
	}
}
