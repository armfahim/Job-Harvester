package io.naztech.jobharvestar.scraper.workday;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * SampoA job site parsing class. <br>
 * URL: https://if.wd3.myworkdayjobs.com/Careers
 * 
 * @author assaduzzaman.sohan
 * @since 2019-02-24
 */
@Service
public class SampoA extends AbstractMyWorkDayJobs {
	private static final String SITE = ShortName.SAMPO_A;
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
