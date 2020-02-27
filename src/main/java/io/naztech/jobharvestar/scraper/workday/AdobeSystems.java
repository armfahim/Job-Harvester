package io.naztech.jobharvestar.scraper.workday;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
/**
 * Adobe Systems job site parsing class. <br>
 * URL: https://adobe.wd5.myworkdayjobs.com/external_experienced
 * 
 * @author assaduzzaman.sohan
 * @since 2019-03-05
 */
@Service
public class AdobeSystems extends AbstractMyWorkDayJobs {
	private static final String SITE = ShortName.ADOBE_SYSTEMS;
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
