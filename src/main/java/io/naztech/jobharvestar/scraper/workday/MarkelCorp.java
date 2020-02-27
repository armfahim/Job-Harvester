package io.naztech.jobharvestar.scraper.workday;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Markel Corp job site parsing class. <br>
 * URL: https://markelcorp.wd5.myworkdayjobs.com/GlobalCareers
 * 
 * @author assaduzzaman.sohan
 * @since 2019-02-17
 */
@Service
public class MarkelCorp extends AbstractMyWorkDayJobs {
	private static final String SITE = ShortName.MARKEL_CORP;
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
