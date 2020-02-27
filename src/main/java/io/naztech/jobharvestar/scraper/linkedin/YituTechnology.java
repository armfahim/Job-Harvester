package io.naztech.jobharvestar.scraper.linkedin;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * YITU Technology URL:
 * https://www.linkedin.com/jobs/search/?f_C=18049607&locationId=OTHERS.worldwide&pageNum=0&position=1
 * 
 * @author rafayet.hossain
 * @since 2019-04-01
 */
@Service
public class YituTechnology extends AbstractLinkedinJobs {

	private static final String SITE = ShortName.YITU_TECHNOLOGY;

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return null;
	}
}
