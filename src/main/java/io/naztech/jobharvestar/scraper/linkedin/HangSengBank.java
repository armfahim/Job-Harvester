package io.naztech.jobharvestar.scraper.linkedin;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Hang Seng Bank <br>
 * URL: https://www.linkedin.com/jobs/search/?f_C=20179&locationId=OTHERS.worldwide&pageNum=0&position=1
 * 
 * @author Tanbirul Hashan
 * @since 2019-03-10
 */
@Service
public class HangSengBank extends AbstractLinkedinJobs {

	private static final String SITE = ShortName.HANG_SENG_BANK;

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return null;
	}
}
