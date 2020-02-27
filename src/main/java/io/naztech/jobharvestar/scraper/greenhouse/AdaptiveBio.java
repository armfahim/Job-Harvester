package io.naztech.jobharvestar.scraper.greenhouse;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.jobharvestar.scraper.greenhouse.AbstractGreenHouse;
import io.naztech.talent.model.SiteMetaData;

/**
 * Adaptive BioTechnologies job site scraper. <br>
 * URL: https://boards.greenhouse.io/embed/job_board?for=adaptivebiotechnologies
 * 
 * @author Asadullah Galib
 * @author iftekar.alam
 * @since 2019-03-11
 */
@Service
public class AdaptiveBio extends AbstractGreenHouse {
	private static final String SITE = ShortName.ADAPTIVE_BIOTECHNOLOGIES;
	private String baseUrl;

	@Override
	protected void setBaseUrl(SiteMetaData site) {
		this.baseUrl = site.getUrl().substring(0, 28);
		super.iframeInDetailPage = true;
		super.filterParm ="gh_jid";
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
