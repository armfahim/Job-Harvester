package io.naztech.jobharvestar.scraper.linkedin;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Letgo job site parser<br> 
 * URL: https://www.linkedin.com/jobs/search/?f_C=10159884&location=United%20States&pageNum=0&position=1
 * 
 * @author Tanbirul Hashan
 * @since 2019-03-13
 */
@Service
public class Letgo extends AbstractLinkedinJobs {

	private static final String SITE = ShortName.LETGO;

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return null;
	}
}
