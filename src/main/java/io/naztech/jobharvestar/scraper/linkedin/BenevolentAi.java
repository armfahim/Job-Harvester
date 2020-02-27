package io.naztech.jobharvestar.scraper.linkedin;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * BenevolentAi <br> 
 * URL: https://www.linkedin.com/jobs/search/?f_C=10929741&locationId=OTHERS.worldwide&pageNum=0&position=1
 * 
 * @author Tanbirul Hashan
 * @since 2019-03-11
 */
@Service
public class BenevolentAi extends AbstractLinkedinJobs {

	private static final String SITE = ShortName.BENEVOLENTAI;

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return null;
	}
}
