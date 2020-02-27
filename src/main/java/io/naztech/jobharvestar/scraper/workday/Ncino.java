package io.naztech.jobharvestar.scraper.workday;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
/**
 * Ncino job site parsing class. <br>
 * URL: https://ncino.wd5.myworkdayjobs.com/nCino
 * 
 * @author assaduzzaman.sohan
 * @since 2019-04-15
 */
@Service
public class Ncino extends AbstractMyWorkDayJobs {
	private static final String SITE = ShortName.NCINO;
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



