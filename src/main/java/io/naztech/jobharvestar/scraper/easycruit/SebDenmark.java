package io.naztech.jobharvestar.scraper.easycruit;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.SiteMetaData;

/**
 * Skand Enskilda Denmark Class <br>
 * URL: https://sebse.easycruit.com/intranet/sebdk/?iso=dk
 * 
 * @author Armaan Seraj Choudhury
 * @author Rahat Ahmad
 * @since 2019-3-3
 */
@Service
public class SebDenmark extends AbstractEasyRecruit {
	private static final String SITE = ShortName.SKAND_ENSKILDA_BANKEN_DENMARK;

	@Override
	public void setBaseUrl(SiteMetaData site) {
		this.baseUrl = site.getUrl().substring(0, 27);
		
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
