package io.naztech.jobharvestar.scraper.linkedin;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Actifio URL:
 * https://www.linkedin.com/jobs/search/?f_C=399246&locationId=OTHERS.worldwide&pageNum=0&position=1
 * 
 * @author rafayet.hossain
 * @since 2019-03-11
 */
@Service
public class Actifio extends AbstractLinkedinJobs {

	private static final String SITE = ShortName.ACTIFIO;

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return null;
	}
}
