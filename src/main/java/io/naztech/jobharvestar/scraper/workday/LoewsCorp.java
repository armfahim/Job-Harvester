package io.naztech.jobharvestar.scraper.workday;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Loews Corp job site parsing class. <br>
 * URL:
 * https://loewscorp.wd1.myworkdayjobs.com/loewscorp/3/refreshFacet/318c8bb6f553100021d223d9780d30be
 * 
 * @author assaduzzaman.sohan
 * @since 2019-02-24
 */
@Service
public class LoewsCorp extends AbstractMyWorkDayJobs {
	private static final String SITE = ShortName.LOEWS_CORP;
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
