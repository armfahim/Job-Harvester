package io.naztech.jobharvestar.scraper.linkedin;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.jobharvestar.scraper.linkedin.AbstractLinkedinJobs;

/**
 * GoldenTree Asset Management job parsing class<br>
 * URL: https://www.linkedin.com/jobs/search?keywords=GoldenTree%20Asset%20Management&location=Worldwide&trk=guest_job_search_jobs-search-bar_search-submit&redirect=false&position=1&pageNum=0
 * 
 * @author Armaan Seraj Choudhury
 * @since 2019-03-10
 */
@Service
public class GoldenTreeAsset extends AbstractLinkedinJobs {
	
	private static final String SITE = ShortName.GOLDENTREE_ASSET_MANAGEMENT;
	
	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return null;
	}
}
