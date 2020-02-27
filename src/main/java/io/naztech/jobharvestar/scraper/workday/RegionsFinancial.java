package io.naztech.jobharvestar.scraper.workday;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * REGIONS FINANCIAL (NEW) job site parsing class. <br>
 * URL: https://regions.wd5.myworkdayjobs.com/Regions_Careers
 * 
 * @author assaduzzaman.sohan
 * @since 2019-02-24
 */
@Service
public class RegionsFinancial extends AbstractMyWorkDayJobs {
	private static final String SITE = ShortName.REGIONS_FINANCIAL_NEW;
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
