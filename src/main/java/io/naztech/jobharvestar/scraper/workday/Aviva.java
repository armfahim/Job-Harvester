package io.naztech.jobharvestar.scraper.workday;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * AVIVA job site parsing class. <br>
 * URL: https://aviva.wd1.myworkdayjobs.com/External
 * 
 * @author assaduzzaman.sohan
 * @since 2019-02-24
 */
@Service
public class Aviva extends AbstractMyWorkDayJobs {
	private static final String SITE = ShortName.AVIVA;
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
