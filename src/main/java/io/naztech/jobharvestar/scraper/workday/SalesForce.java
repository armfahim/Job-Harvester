package io.naztech.jobharvestar.scraper.workday;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * SalesForce job site parsing class. <br>
 * URL: https://salesforce.wd1.myworkdayjobs.com/External_Career_Site
 * 
 * @author assaduzzaman.sohan
 * @since 2019-03-05
 */
@Service
public class SalesForce extends AbstractMyWorkDayJobs {
	private static final String SITE = ShortName.SALESFORCE;
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
