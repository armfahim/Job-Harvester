package io.naztech.jobharvestar.scraper.indeed;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.jobharvestar.scraper.indeed.AbstractIndeed;
import io.naztech.talent.model.SiteMetaData;

/**
 * Alan  Job parse  <br>
 * https://www.indeed.com/jobs?q=Alan
 * 
 * @author jannatul.maowa
 * @since 2019-03-31
 */
@Service
public class Alan extends AbstractIndeed{

	private static final String SITE = ShortName.ALAN;
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