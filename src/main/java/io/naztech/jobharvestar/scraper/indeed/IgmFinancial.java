package io.naztech.jobharvestar.scraper.indeed;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.SiteMetaData;

/**
 * IGM FINANCIAL URL: https://ca.indeed.com/Igm-Financial-jobs
 * 
 * @author Tanbirul Hashan
 * @since 2019-02-17
 */
@Service
public class IgmFinancial extends AbstractIndeed {

	private static final String SITE = ShortName.IGM_FINANCIAL;
	private String baseUrl;

	@Override
	public void setBaseUrl(SiteMetaData site) {
		this.baseUrl = site.getUrl().substring(0, 21);
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
