package io.naztech.jobharvestar.scraper.workday;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
/**
 * CYLANCE job site parser<br>
 * URL: https://bb.wd3.myworkdayjobs.com/Cylance
 * 
 * @author assaduzzaman.sohan
 * @since 2019-03-11
 */
@Service
public class Cylance extends AbstractMyWorkDayJobs {
	private static final String SITE = ShortName.CYLANCE;
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



