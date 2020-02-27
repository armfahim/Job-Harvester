package io.naztech.jobharvestar.scraper.indeed;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.SiteMetaData;

/**
 * Pacific Investment Management job sites scrapper
 * URL: https://www.indeed.com/q-Pacific-Investment-Management-Company-jobs.html
 * 
 * @author Tanbirul Hashan
 * @since 2019-03-07
 */
@Service
public class PacificInvestmentManagement extends AbstractIndeed {

	private static final String SITE = ShortName.PACIFIC_INVESTMENT_MANAGEMENT_CO;
	private String baseUrl;

	@Override
	public void setBaseUrl(SiteMetaData site) {
		this.baseUrl = site.getUrl().substring(0, 22);
	}

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return this.baseUrl;
	}
}
