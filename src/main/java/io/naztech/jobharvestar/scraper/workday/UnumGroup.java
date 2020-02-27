package io.naztech.jobharvestar.scraper.workday;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Unum Group job site parsing class. <br>
 * URL: https://unum.wd1.myworkdayjobs.com/External
 * 
 * @author assaduzzaman.sohan
 * @since 2019-02-24
 */
@Service
public class UnumGroup extends AbstractMyWorkDayJobs {
	private static final String SITE = ShortName.UNUM_GROUP;
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
