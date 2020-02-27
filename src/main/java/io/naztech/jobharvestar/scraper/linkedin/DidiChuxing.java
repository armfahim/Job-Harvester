package io.naztech.jobharvestar.scraper.linkedin;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Didi Chuxing
 * URL: https://www.linkedin.com/jobs/didi-chuxing-jobs?pageNum=0&position=1
 * 
 * @author rafayet.hossain
 * @since 2019-03-11
 */

@Service
public class DidiChuxing extends AbstractLinkedinJobs {
	private static final String SITE = ShortName.DIDI_CHUXING;

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return null;
	}
}
