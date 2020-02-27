package io.naztech.jobharvestar.scraper.workday;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * M&T Bank Corp job site parsing class. <br>
 * URL: https://mtb.wd5.myworkdayjobs.com/MTB
 * 
 * @author assaduzzaman.sohan
 * @since 2019-02-24
 */
@Service
public class MtBankCorp extends AbstractMyWorkDayJobs {
	private static final String SITE = ShortName.M_N_T_BANK_CORP;
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
