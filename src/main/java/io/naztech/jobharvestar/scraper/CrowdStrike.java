package io.naztech.jobharvestar.scraper;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.jobharvestar.scraper.workday.AbstractMyWorkDayJobs;
/**
 * CrowdStrike Jobsite Parser <br>
 * URL: https://crowdstrike.wd5.myworkdayjobs.com/crowdstrikecareers
 * 
 * @author Rahat Ahmad
 * @since 2019-03-13
 * 
 */
@Service
public class CrowdStrike extends AbstractMyWorkDayJobs {
	private static final String SITE = ShortName.CROWDSTRIKE;
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
