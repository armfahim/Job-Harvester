package io.naztech.jobharvestar.scraper.workday;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
/**
 * Aegon job site parsing class. <br>
 * URL: https://transamerica.wd5.myworkdayjobs.com/AUK_JobSite
 * 
 * @author assaduzzaman.sohan
 * @since 2019-02-20
 */
@Service
public class Aegon extends AbstractMyWorkDayJobs {
	private static final String SITE = ShortName.AEGON;
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



