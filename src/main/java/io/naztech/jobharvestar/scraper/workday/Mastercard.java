package io.naztech.jobharvestar.scraper.workday;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
/**
 * Mastercard job site parsing class. <br>
 * URL: https://mastercard.wd1.myworkdayjobs.com/CorporateCareers
 * 
 * @author assaduzzaman.sohan
 * @since 2019-03-05
 */
@Service
public class Mastercard extends AbstractMyWorkDayJobs {
	private static final String SITE = ShortName.MASTERCARD;
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



