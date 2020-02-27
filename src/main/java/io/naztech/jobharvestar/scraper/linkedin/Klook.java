package io.naztech.jobharvestar.scraper.linkedin;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * klook <br> 
 * URL: https://www.linkedin.com/jobs/search/?f_C=3829760&locationId=OTHERS.worldwide&pageNum=0&position=1
 * 
 * @author Tanbirul Hashan
 * @since 2019-03-13
 */
@Service
public class Klook extends AbstractLinkedinJobs {

	private static final String SITE = ShortName.KLOOK;

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return null;
	}
}
