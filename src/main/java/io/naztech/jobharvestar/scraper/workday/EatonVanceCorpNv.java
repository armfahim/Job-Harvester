package io.naztech.jobharvestar.scraper.workday;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * EATON VANCE CORP NV job site parsing class. <br>
 * URL: https://eatonvance.wd5.myworkdayjobs.com/Professional
 * 
 * @author assaduzzaman.sohan
 * @since 2019-02-20
 */
@Service
public class EatonVanceCorpNv extends AbstractMyWorkDayJobs{
	private static final String SITE = ShortName.EATON_VANCE_CORP_NV;
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

