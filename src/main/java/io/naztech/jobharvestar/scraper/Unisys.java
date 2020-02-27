package io.naztech.jobharvestar.scraper;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.jobharvestar.scraper.workday.AbstractMyWorkDayJobs;

/**
 * Unisys job site parser. <br>
 * URL: https://unisys.wd5.myworkdayjobs.com/External
 * 
 * @author tanmoy.tushar
 * @since 2019-10-21
 */
@Service
public class Unisys extends AbstractMyWorkDayJobs {
	private static final String SITE = ShortName.UNISYS;

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return null;
	}
}
