package io.naztech.jobharvestar.scraper.workday;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Sun Life Financial job site parsing class. <br>
 * URL: https://sunlife.wd3.myworkdayjobs.com/Experienced-Jobs
 * 
 * @author assaduzzaman.sohan
 * @since 2019-02-24
 */
@Service
public class SunLifeFinancial extends AbstractMyWorkDayJobs {

	private static final String SITE = ShortName.SUN_LIFE_FINANCIAL;
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
