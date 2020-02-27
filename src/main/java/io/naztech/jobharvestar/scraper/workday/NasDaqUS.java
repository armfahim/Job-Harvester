package io.naztech.jobharvestar.scraper.workday;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * NasDaq US job site parsing class. <br>
 * URL: https://nasdaq.wd1.myworkdayjobs.com/US_External_Career_Site
 * 
 * @author assaduzzaman.sohan
 * @since 2019-02-26
 */
@Service
public class NasDaqUS extends AbstractMyWorkDayJobs {
	private static final String SITE = ShortName.NASDAQ;
	private String baseUrl;

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return baseUrl;
	}
}
