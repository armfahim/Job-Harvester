package io.naztech.jobharvestar.scraper.workday;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
/**
 * Tech Style job site parsing class. <br>
 * URL: https://justfab.wd1.myworkdayjobs.com/justfabcareers
 * 
 * @author assaduzzaman.sohan
 * @since 2019-02-20
 */
@Service
public class TechStyle extends AbstractMyWorkDayJobs {
	private static final String SITE = ShortName.TECHSTYLE_FASHION_GROUP;
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



