package io.naztech.jobharvestar.scraper.workday;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Athene Hldg Ltd job site parsing class. <br>
 * URL: https://athene.wd5.myworkdayjobs.com/athene_careers
 * 
 * @author assaduzzaman.sohan
 * @since 2019-02-24
 */
@Service
public class AtheneHldgLtd extends AbstractMyWorkDayJobs {
	private static final String SITE = ShortName.ATHENE_HLDG_LTD_CLASS_A;
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
