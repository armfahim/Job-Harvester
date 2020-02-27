package io.naztech.jobharvestar.scraper.workday;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * MIZUHO FINANCIAL GROUP AMERICAS job site parsing class. <br>
 * URL: https://mizuho.wd1.myworkdayjobs.com/mizuhoamericas
 * 
 * @author assaduzzaman.sohan
 * @since 2019-02-26
 */
@Service
public class MizuhoFinancialGroup extends AbstractMyWorkDayJobs {
	private static final String SITE  = ShortName.MIZUHO_FINANCIAL_GROUP_AMERICAS;
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