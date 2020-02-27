package io.naztech.jobharvestar.scraper.linkedin;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.jobharvestar.scraper.linkedin.AbstractLinkedinJobs;

/**
 * Lone Pine Capital job parsing class<br>
 * URL:  https://www.linkedin.com/jobs/search?keywords=Lone%20Pine%20Capital&location=Worldwide&trk=guest_job_search_jobs-search-bar_search-submit&redirect=false&position=1&pageNum=0
 * NEED TO CHECK JOB ABILABILITY. NOT GRANTTED TO WORK CORRECTLY OR NOT
 * 
 * @author BM Al-Amin
 * @since: 2019-03-06
 */
@Service
public class LonePineCapital extends AbstractLinkedinJobs{
	private static final String SITE = ShortName.LONE_PINE_CAPITAL;
	
	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return null;
	}
}
