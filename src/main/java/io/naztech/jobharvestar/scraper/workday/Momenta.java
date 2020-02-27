package io.naztech.jobharvestar.scraper.workday;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
/**
 * Momenta job site parsing class. <br>
 * URL: https://osv-momentapharma.wd1.myworkdayjobs.com/MomentaCareers
 * 
 * @author assaduzzaman.sohan
 * @since 2019-03-10
 */
@Service
public class Momenta extends AbstractMyWorkDayJobs {
	private static final String SITE = ShortName.MOMENTA;
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



