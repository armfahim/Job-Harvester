package io.naztech.jobharvestar.scraper.linkedin;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Udaan URL: https://in.linkedin.com/jobs/udaan-com-jobs?pageNum=0&position=1
 * 
 * @author rafayet.hossain
 * @since 2019-03-11
 */
@Service
public class Udaan extends AbstractLinkedinJobs {

	private static final String SITE = ShortName.UDAAN;

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return null;
	}
}
