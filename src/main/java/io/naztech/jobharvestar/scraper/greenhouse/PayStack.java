package io.naztech.jobharvestar.scraper.greenhouse;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.jobharvestar.scraper.greenhouse.AbstractGreenHouse;
import io.naztech.talent.model.SiteMetaData;

/**
 * PayStack job site parser<br>
 * URL: https://boards.greenhouse.io/paystack
 * 
 * @author Arifur Rahman
 * @since 2019-03-23
 */
@Service
public class PayStack extends AbstractGreenHouse {
	private static final String SITE = ShortName.PAYSTACK;
	
	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return this.baseUrl;
	}

	@Override
	protected void setBaseUrl(SiteMetaData site) {
		this.baseUrl = site.getUrl().substring(0, 28);
	}
}
