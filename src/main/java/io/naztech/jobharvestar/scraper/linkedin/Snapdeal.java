package io.naztech.jobharvestar.scraper.linkedin;
import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * SnapDeal
 * URL: https://in.linkedin.com/jobs/snapdeal-jobs?pageNum=0&position=1
 * 
 * @author rafayet.hossain
 * @since 2019-04-01
 */
@Service
public class Snapdeal extends AbstractLinkedinJobs {

	private static final String SITE = ShortName.SNAPDEAL;

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return null;
	}
}
