package io.naztech.jobharvestar.scraper.linkedin;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Quovo<br>
 * URL:
 * https://www.linkedin.com/jobs/search?keywords=Quovo&location=&trk=guest_job_search_jobs-search-bar_search-submit&redirect=false&position=1&pageNum=0
 * 
 * @author tohedul.islum
 * @since 2019-03-31
 */
@Service
public class Quovo extends AbstractLinkedinJobs {
	private static final String SITE = ShortName.QUOVO;

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return null;
	}
}
