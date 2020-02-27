package io.naztech.jobharvestar.scraper.workday;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * STATE STREET CORP job site parsing class. <br>
 * URL: https://statestreet.wd1.myworkdayjobs.com/Global
 * 
 * @author assaduzzaman.sohan
 * @since 2019-02-26
 */
@Service
public class StateStreetCorp extends AbstractMyWorkDayJobs {
	private static final String SITE  = ShortName.STATE_STREET_CORP;
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