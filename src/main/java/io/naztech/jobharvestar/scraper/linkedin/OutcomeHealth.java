package io.naztech.jobharvestar.scraper.linkedin;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Outcome Health URL:
 * https://www.linkedin.com/jobs/search?locationId=OTHERS.worldwide&f_C=141332&trk=jobs_jserp_pagination_2&start=25&count=25&pageNum=1&position=1
 * 
 * @author rafayet.hossain
 * @since 2019-04-01
 */
@Service
public class OutcomeHealth extends AbstractLinkedinJobs {

	private static final String SITE = ShortName.OUTCOME_HEALTH;

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return null;
	}
}
