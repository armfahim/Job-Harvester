package io.naztech.jobharvestar.scraper.workday;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
/**
 * QBE Insurance Group job site parsing class. <br>
 * URL: https://qbe.wd3.myworkdayjobs.com/QBE-Careers
 * 
 * @author assaduzzaman.sohan
 * @since 2019-02-24
 */
@Service
public class QbeInsuranceGroup extends AbstractMyWorkDayJobs {
	private static final String SITE = ShortName.QBE_INSURANCE_GROUP;
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
