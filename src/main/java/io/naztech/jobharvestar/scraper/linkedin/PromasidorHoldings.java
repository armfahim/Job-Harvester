package io.naztech.jobharvestar.scraper.linkedin;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Promasidor Holdings job site parser
 * URL: https://www.linkedin.com/jobs/search?locationId=OTHERS.worldwide&f_C=95032&trk=companyTopCard_top-card-button&pageNum=0&position=1
 * 
 * @author rafayet.hossain
 * @since 2019-04-01
 */
@Service
public class PromasidorHoldings extends AbstractLinkedinJobs {

	private static final String SITE = ShortName.PROMASIDOR_HOLDINGS;

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return null;
	}
}
