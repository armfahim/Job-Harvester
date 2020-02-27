package io.naztech.jobharvestar.scraper.greenhouse;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.SiteMetaData;
/**
 * LEX job site parser.<br> 
 * URL: https://boards.greenhouse.io/embed/job_board?for=iex&b=https%3A%2F%2Fiextrading.com%2Fcareers%2F
 * 
 * @author kamrul.islam
 * @since 2019-03-27
 */
@Service
public class Lex extends  AbstractGreenHouse {
	private static final String SITE = ShortName.LEX;

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