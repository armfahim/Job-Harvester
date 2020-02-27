package io.naztech.jobharvestar.scraper.workday;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * NasDaq Global job site parsing class. <br>
 * URL: https://nasdaq.wd1.myworkdayjobs.com/en-US/Global_External_Site
 * 
 * @author assaduzzaman.sohan
 * @since 2019-02-26
 */
@Service
public class NasDaqGlobal extends AbstractMyWorkDayJobs {
	private static final String SITE = ShortName.NASDAQ_GLOBAL;
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