package io.naztech.jobharvestar.scraper.greenhouse;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.jobharvestar.scraper.greenhouse.AbstractGreenHouse;
import io.naztech.talent.model.SiteMetaData;

/**
 * Netskope Job Site Parser<br>
 * URL: https://boards.greenhouse.io/embed/job_board?for=netskope
 * 
 * @author Rahat Ahmad
 * @author bm.alamin
 * @author iftekar.alam
 * @since 2019-03-11
 */
@Service
public class Netskope extends AbstractGreenHouse {

	private static final String SITE = ShortName.NETSKOPE;

	@Override
	protected void setBaseUrl(SiteMetaData site) {
		this.baseUrl = site.getUrl().substring(0, 28);
		super.iframeInDetailPage = true;
		super.filterParm = "gh_jid";
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
