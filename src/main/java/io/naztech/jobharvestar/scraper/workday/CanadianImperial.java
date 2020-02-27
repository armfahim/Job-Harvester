package io.naztech.jobharvestar.scraper.workday;

import org.springframework.stereotype.Service;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.jobharvestar.scraper.workday.AbstractMyWorkDayJobs;

/**
 * Imperial Bank Job site Scraper<br>
 * URL: https://cibc.wd3.myworkdayjobs.com/search
 * 
 * @author Mahmud Rana
 * @author tanmoy.tushar
 * @since 2019-02-03
 */
@Service
public class CanadianImperial extends AbstractMyWorkDayJobs {
	private static final String SITE = ShortName.CANADIAN_IMPERIAL_BANK;

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return null;
	}	
}
