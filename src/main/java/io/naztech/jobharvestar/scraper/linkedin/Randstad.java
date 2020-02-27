package io.naztech.jobharvestar.scraper.linkedin;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.jobharvestar.scraper.linkedin.AbstractLinkedinJobs;

/**
 * RANDSTAD job site parser. <br> 
 * URL: https://www.linkedin.com/jobs/search?locationId=OTHERS.worldwide&f_C=2327&redirect=false&position=1&pageNum=0
 * 
 * @author tohedul.islum
 * @author tanmoy.tushar
 * @since 2019-03-07
 */
@Service
public class Randstad extends AbstractLinkedinJobs {
	private static final String SITE = ShortName.RANDSTAD_NV;

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return null;
	}
	
}
