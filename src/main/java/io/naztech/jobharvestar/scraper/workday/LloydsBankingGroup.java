package io.naztech.jobharvestar.scraper.workday;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * LLOYDS BANKING GROUP job site parsing class. <br>
 * URL: https://lbg.wd3.myworkdayjobs.com/lbg_Careers
 * 
 * @author assaduzzaman.sohan
 * @since 2019-02-26
 */
@Service
public class LloydsBankingGroup extends AbstractMyWorkDayJobs {
	private static final String SITE  = ShortName.LLOYDS_BANKING_GROUP;
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