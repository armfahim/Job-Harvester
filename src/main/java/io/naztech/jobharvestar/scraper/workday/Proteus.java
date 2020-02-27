package io.naztech.jobharvestar.scraper.workday;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.jobharvestar.scraper.workday.AbstractMyWorkDayJobs;

/**
 * Proteus job site parser<br>
 * URL: https://proteus.wd5.myworkdayjobs.com/Proteus
 * 
 * @author tohedul.islum
 * @author iftekar.alam
 * @since 2019-03-14
 */

@Service
public class Proteus extends AbstractMyWorkDayJobs {
	private static final String SITE = ShortName.PROTEUS_DIGITAL_HEALTH;
	private String baseUrl;
	
	@Override
	protected String getJobCountElementId() {
		return "wd-FacetedSearchResultList-PaginationText-facetSearchResultList.jobProfile.data";
	}

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return baseUrl;
	}
}