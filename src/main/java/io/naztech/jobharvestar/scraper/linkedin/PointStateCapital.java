package io.naztech.jobharvestar.scraper.linkedin;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Point State Capital <br> 
 * URL: https://www.linkedin.com/jobs/point-state-capital-jobs?pageNum=0&position=1
 * 
 * @author Tanbirul Hashan
 * @author bm.alamin
 * @since 2019-03-10
 */
@Service
public class PointStateCapital extends AbstractLinkedinJobs {

	private static final String SITE = ShortName.POINTSTATE_CAPITAL;

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return null;
	}
}
