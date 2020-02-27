package io.naztech.jobharvestar.scraper.glassdoor;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.jobharvestar.scraper.linkedin.AbstractLinkedinJobs;

/**
 * BAUPOST GROUP job parsing class<br>
 * URL: https://www.linkedin.com/jobs/search?keywords=The%20Baupost%20Group&location=Worldwide&trk=guest_job_search_jobs-search-bar_search-submit&redirect=false&position=1&pageNum=0
 * 
 * @author BM Al-Amin
 * @since: 2019-03-05
 */
@Service
public class BaupostGroup extends AbstractLinkedinJobs{
	private static final String SITE = ShortName.BAUPOST_GROUP;
	
	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return null;
	}
}
