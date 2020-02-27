package io.naztech.jobharvestar.scraper.indeed;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.SiteMetaData;

/**
 * FNF GROUP job site parsing class
 * URL: https://www.indeed.com/jobs?q=Fnf&l=
 * 
 * @author Tanbirul Hashan
 * @since 2019-02-14
 */
@Service
public class FnfGroup extends AbstractIndeed {
	private static final String SITE = ShortName.FNF_GROUP;
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
