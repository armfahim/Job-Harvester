package io.naztech.jobharvestar.scraper.linkedin;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Ant Financial URL:
 * https://www.linkedin.com/jobs/search/?f_C=15245628%2C167072&locationId=OTHERS.worldwide&pageNum=0&position=1
 * 
 * @author rafayet.hossain
 * @since 2019-04-01
 */
@Service
public class AntFinancial extends AbstractLinkedinJobs {

	private static final String SITE = ShortName.ANT_FINANCIAL;

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return null;
	}
}
