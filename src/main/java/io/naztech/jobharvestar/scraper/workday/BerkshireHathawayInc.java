package io.naztech.jobharvestar.scraper.workday;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.jobharvestar.scraper.workday.AbstractMyWorkDayJobs;

/**
 * Berkshire Hathaway job site parser. <br>
 * URL: https://nationalindemnity.wd5.myworkdayjobs.com/BHHC
 * 
 * @author Benajir Ullah
 * @author bm.alamin
 * @author tanmoy.tushar
 * @since 2019-02-11
 */
@Service
public class BerkshireHathawayInc extends AbstractMyWorkDayJobs {
	private static final String SITE = ShortName.BERKSHIRE_HATHAWAY_INC;

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return null;
	}

	@Override
	protected String getJobCountElementId() {
		return "wd-FacetedSearchResultList-facetSearchResultList.newFacetSearch.Report_Entry";
	}
}
