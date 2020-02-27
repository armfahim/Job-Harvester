package io.naztech.jobharvestar.scraper.workday;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
/**
 * VOYA FINANCIAL job site parsing class. <br>
 * URL: https://godirect.wd5.myworkdayjobs.com/en-US/voya_jobs
 * 
 * @author assaduzzaman.sohan
 * @since 2019-02-27
 */
@Service
public class VoyaFinancialWorkDays extends AbstractMyWorkDayJobs {
	private static final String SITE = ShortName.VOYA_FINANCIAL_WORKDAY;
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



