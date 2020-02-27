package io.naztech.jobharvestar.scraper.bbva;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.SiteMetaData;

/**
 * BBVA Uruguay job site scrapper. <br>
 * URL: https://careers.bbva.com/uruguay/jobs-results/
 * 
 * @author naym.hossain
 * @since 2019-01-24
 */
@Service
public class BbvaUruguay extends AbstractBbva {
	private static final String SITE = ShortName.BBVA_URUGUAY;
	private String baseUrl;

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected void setBaseUrl(SiteMetaData site) {
		this.baseUrl = site.getUrl().substring(0, 33);
	}

	@Override
	protected String getBaseUrl() {
		return this.baseUrl;
	}
}
