package io.naztech.jobharvestar.scraper.indeed;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.SiteMetaData;

/**
 * MANULIFE FINANCIAL CORP
 * URL:https://www.indeed.com/q-Manulife-Financial-jobs.html
 * 
 * @author Tanbirul Hashan
 * @since 2019-02-12
 */

@Service
public class ManuLifeFinacialCorp extends AbstractIndeed {

	private static final String SITE = ShortName.MANULIFE_FINANCIAL_CORP;
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
