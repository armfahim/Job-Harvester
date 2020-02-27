package io.naztech.jobharvestar.scraper.workday;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Julius Baer Group job site parsing class. <br>
 * URL: https://juliusbaer.wd3.myworkdayjobs.com/en-US/External
 * 
 * @author assaduzzaman.sohan
 * @since 2019-02-20
 */
@Service
public class JuliusBaerGroup extends AbstractMyWorkDayJobs{

	private static final String SITE = ShortName.JULIUS_BAER_GROUP;
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



