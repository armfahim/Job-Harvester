package io.naztech.jobharvestar.scraper.greenhouse;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.SiteMetaData;
/**
 * RobinHood job Site Parser<br>
 * URL: https://boards.greenhouse.io/embed/job_board?for=robinhood&b=https%3A%2F%2Fcareers.robinhood.com%2Fopenings
 * 
 * @author Arifur Rahman
 * @author kamrul.islam
 * @since 2019-03-20
 */

@Service
public class Robinhood extends AbstractGreenHouse {
	private static final String SITE = ShortName.ROBINHOOD;

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
		super.filterParm ="gh_jid";
		
		this.baseUrl = site.getUrl().substring(0, 28);
	}
}

