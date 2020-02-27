package io.naztech.jobharvestar.scraper.workday;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
/**
 * WELLINGTON ALTERNATIVE INVESTMENTS job site parsing class. <br>
 * URL: https://wellington.wd5.myworkdayjobs.com/External
 * 
 * @author assaduzzaman.sohan
 * @since 2019-03-07
 */
@Service
public class WellingtonAlternativeInvestments extends AbstractMyWorkDayJobs {
	private static final String SITE = ShortName.WELLINGTON_ALTERNATIVE_INVESTMENTS;
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



