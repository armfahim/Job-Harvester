package io.naztech.jobharvestar.scraper.linkedin;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * App Direct <br> 
 * URL: https://www.linkedin.com/jobs/search/?f_C=1418447&locationId=OTHERS.worldwide&pageNum=0&position=1
 * 
 * @author Tanbirul Hashan
 * @since 2019-03-11
 */
@Service
public class AppDirect extends AbstractLinkedinJobs {

	private static final String SITE = ShortName.APPDIRECT;

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return null;
	}
}
