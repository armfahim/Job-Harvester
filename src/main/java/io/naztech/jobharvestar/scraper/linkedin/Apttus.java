package io.naztech.jobharvestar.scraper.linkedin;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Apttus <br> 
 * URL: https://www.linkedin.com/jobs/search/?f_C=1181097&locationId=OTHERS.worldwide&pageNum=0&position=1
 * 
 * @author Tanbirul Hashan
 * @since 2019-03-11
 */
@Service
public class Apttus extends AbstractLinkedinJobs {

	private static final String SITE = ShortName.APTTUS;

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return null;
	}
}
