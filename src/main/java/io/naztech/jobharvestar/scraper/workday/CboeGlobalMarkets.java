package io.naztech.jobharvestar.scraper.workday;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * CBOE GLOBAL MARKETS US job site parsing class. <br>
 * URL: https://cboe.wd1.myworkdayjobs.com/External_Career_CBOE
 * 
 * @author assaduzzaman.sohan
 * @since 2019-02-26
 */
@Service
public class CboeGlobalMarkets extends AbstractMyWorkDayJobs {
	private static final String SITE = ShortName.CBOE_GLOBAL_MARKETS;
	private String baseUrl;

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return baseUrl;
	}

	@Override
	protected String getJobCountElementId() {
		return "wd-FacetedSearchResultList-PaginationText-facetSearchResultList.jobProfile.data";
	}
}
