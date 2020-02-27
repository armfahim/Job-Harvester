package io.naztech.jobharvestar.scraper.workday;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * DIRECT LINE INS GRP job site parsing class. <br>
 * URL: https://dlg.wd3.myworkdayjobs.com/DLGCAREERS
 * 
 * @author assaduzzaman.sohan
 * @since 2019-02-20
 */
@Service
public class DirectLineInsuranceGroup extends AbstractMyWorkDayJobs {
	private static final String SITE = ShortName.DIRECT_LINE_INS_GRP;
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
