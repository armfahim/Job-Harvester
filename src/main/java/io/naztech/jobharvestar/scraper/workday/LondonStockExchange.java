package io.naztech.jobharvestar.scraper.workday;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * LONDON STOCK EXCHANGE job site parsing class. <br>
 * URL: https://lseg.wd3.myworkdayjobs.com/LSEG_Careers
 * 
 * @author assaduzzaman.sohan
 * @since 2019-02-26
 */
@Service
public class LondonStockExchange extends AbstractMyWorkDayJobs {
	private static final String SITE  = ShortName.LONDON_STOCK_EXCHANGE;
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
