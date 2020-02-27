package io.naztech.jobharvestar.scraper.linkedin;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Zenefits URL:
 * https://www.linkedin.com/jobs/search/?f_C=2997680&locationId=OTHERS.worldwide&pageNum=0&position=1
 * 
 * @author rafayet.hossain
 * @since 2019-04-01
 */
@Service
public class Zenefits extends AbstractLinkedinJobs {

	private static final String SITE = ShortName.ZENEFITS;

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return null;
	}
}
