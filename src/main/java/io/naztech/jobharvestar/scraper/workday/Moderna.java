package io.naztech.jobharvestar.scraper.workday;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
/**
 * Moderna job site parsing class. <br>
 * URL: https://modernatx.wd1.myworkdayjobs.com/M_tx
 * 
 * @author assaduzzaman.sohan
 * @since 2019-03-12
 */
@Service
public class Moderna extends AbstractMyWorkDayJobs {
	private static final String SITE = ShortName.MODERNA;
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



