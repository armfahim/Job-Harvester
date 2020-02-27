package io.naztech.jobharvestar.scraper.linkedin;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Coinbase <br> 
 * URL: https://www.linkedin.com/jobs/search/?f_C=2857634&locationId=OTHERS.worldwide&pageNum=0&position=1
 * 
 * @author Tanbirul Hashan
 * @since 2019-03-12
 */
@Service
public class Coinbase extends AbstractLinkedinJobs {

	private static final String SITE = ShortName.COINBASE;

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return null;
	}
}
