package io.naztech.jobharvestar.scraper.workday;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * American Intl Group job site parsing class. <br>
 * URL: https://aig.wd1.myworkdayjobs.com/aig
 * 
 * @author assaduzzaman.sohan
 * @since 2019-02-24
 */
@Service
public class AmericanIntlGroup extends AbstractMyWorkDayJobs {
	private static final String SITE = ShortName.AMERICAN_INTL_GROUP;
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
