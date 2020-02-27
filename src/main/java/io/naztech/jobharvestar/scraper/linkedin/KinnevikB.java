package io.naztech.jobharvestar.scraper.linkedin;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * KINNEVIK B <br>
 * URL: https://www.linkedin.com/jobs/search/?f_C=2710452&locationId=OTHERS.worldwide&pageNum=0&position=1
 * 
 * @author Tanbirul Hashan
 * @since 2019-03-10
 */
@Service
public class KinnevikB extends AbstractLinkedinJobs {

	private static final String SITE = ShortName.KINNEVIK_B;

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return null;
	}

}
