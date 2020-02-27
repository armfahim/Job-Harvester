package io.naztech.jobharvestar.scraper.linkedin;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Quikr URL:
 * https://www.linkedin.com/jobs/search?locationId=OTHERS.worldwide&f_C=673276&trk=companyTopCard_top-card-button&pageNum=0&position=1
 * 
 * @author rafayet.hossain
 * @since 2019-04-01
 */
@Service
public class Quikr extends AbstractLinkedinJobs {

	private static final String SITE = ShortName.QUIKR;

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return null;
	}
}
