package io.naztech.jobharvestar.scraper.workday;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Tal Dai Ichi Australia job site parsing class. <br>
 * URL: https://tal.wd3.myworkdayjobs.com/TAL-current-opportunities
 * 
 * @author assaduzzaman.sohan
 * @since 2019-02-24
 */
@Service
public class TalDaiIchiAustralia extends AbstractMyWorkDayJobs {
	private static final String SITE = ShortName.TAL_DAI_ICHI_AUSTRALIA;
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
