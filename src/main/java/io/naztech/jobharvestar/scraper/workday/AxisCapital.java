package io.naztech.jobharvestar.scraper.workday;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * AXIS CAPITAL HOLDINGS job site parsing class. <br>
 * URL: https://axiscapital.wd1.myworkdayjobs.com/axiscareers
 * 
 * @author assaduzzaman.sohan
 * @since 2019-02-24
 */
@Service
public class AxisCapital extends AbstractMyWorkDayJobs {
	private static final String SITE = ShortName.AXIS_CAPITAL_HOLDINGS;
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
