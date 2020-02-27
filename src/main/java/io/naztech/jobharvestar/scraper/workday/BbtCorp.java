package io.naztech.jobharvestar.scraper.workday;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * BB&T Corp job site parsing class. <br>
 * URL: https://bbt.wd1.myworkdayjobs.com/Careers
 * 
 * @author assaduzzaman.sohan
 * @since 2019-02-24
 */
@Service
public class BbtCorp extends AbstractMyWorkDayJobs {
	private static final String SITE = ShortName.BBNT_CORP;
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
