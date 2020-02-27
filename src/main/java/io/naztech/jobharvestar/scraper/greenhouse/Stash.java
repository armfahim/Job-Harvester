package io.naztech.jobharvestar.scraper.greenhouse;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.SiteMetaData;
/**
 * STASH job site parser.<br>
 * URL: https://boards.greenhouse.io/embed/job_board?for=stashinvest&b=https%3A%2F%2Fwww.stashinvest.com%2Fcareers
 * 
 * @author Fahim Reza
 * @author kamrul.islam
 * @since 2019-04-02
 */
@Service
public class Stash extends AbstractGreenHouse {
	private static final String SITE = ShortName.STASH;

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
		super.iframeInDetailPage = true;
		super.filterParm = "/job";
		
		this.baseUrl = site.getUrl().substring(0, 28);
	}
}
