package io.naztech.jobharvestar.scraper.greenhouse;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.jobharvestar.scraper.greenhouse.AbstractGreenHouse;
import io.naztech.talent.model.SiteMetaData;
/**
 * JUUL Labs job site parser.<br>
 * URL: https://boards.greenhouse.io/juullabs
 * 
 * @author jannatul.maowa
 * @author tanmoy.tushar
 * @since 2019-05-02
 */
@Service
public class JuulLabs extends AbstractGreenHouse {
	private static final String SITE = ShortName.JUUL_LABS;

	@Override
	protected void setBaseUrl(SiteMetaData site) {
		this.baseUrl = site.getUrl().substring(0, 28);
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
