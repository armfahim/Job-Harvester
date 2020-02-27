package io.naztech.jobharvestar.scraper.greenhouse;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.jobharvestar.scraper.greenhouse.AbstractGreenHouse;
import io.naztech.talent.model.SiteMetaData;
/**
 * Jumo job site parsing class. <br>
 * URL: https://boards.greenhouse.io/embed/job_board?for=jumo&b=https%3A%2F%2Fwww.jumo.com%2Fcareers%2Fpositions
 * 
 * @author Kamrul.islam
 * @since 2019-04-15
 */
@Service
public class Jumo extends AbstractGreenHouse {
	private static final String SITE = ShortName.JUMO;

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
