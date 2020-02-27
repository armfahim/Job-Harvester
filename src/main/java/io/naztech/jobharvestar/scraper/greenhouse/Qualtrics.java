package io.naztech.jobharvestar.scraper.greenhouse;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.SiteMetaData;

/**
 * Qualtrics job site parsing class. <br>
 * URL: https://boards.greenhouse.io/qualtrics
 * 
 * @author assaduzzaman.sohan
 * @author kamrul.islam
 * @since 2019-03-13
 */
@Service
public class Qualtrics extends AbstractGreenHouse {
	private static final String SITE = ShortName.QUALTRICS;
	
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
