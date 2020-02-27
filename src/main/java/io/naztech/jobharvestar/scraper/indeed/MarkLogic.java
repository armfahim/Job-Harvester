package io.naztech.jobharvestar.scraper.indeed;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.SiteMetaData;

/**
 * MarkLogic Job site Parser<br>
 * URL: https://www.indeed.com/q-Marklogic-jobs.html
 * 
 * @author rahat.ahmad
 * @since 2019-03-14
 * 
 * @author bm.alamin
 */

@Service
public class MarkLogic extends AbstractIndeed {
	private static final String SITE = ShortName.MARKLOGIC;
	private String baseUrl;
	
	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return this.baseUrl;
	}

	@Override
	public void setBaseUrl(SiteMetaData site) {
		this.baseUrl = site.getUrl().substring(0, 22);
	}

}
