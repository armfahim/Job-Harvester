package io.naztech.jobharvestar.scraper.workday;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
/**
 * SmsAssist job site parsing class. <br>
 * URL: https://smsassist.wd5.myworkdayjobs.com/SMSAssistcareers
 * 
 * @author assaduzzaman.sohan
 * @since 2019-03-14
 */
@Service
public class SmsAssist extends AbstractMyWorkDayJobs {
	private static final String SITE = ShortName.SMS_ASSIST;
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
