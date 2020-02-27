package io.naztech.jobharvestar.scraper.greenhouse;

import org.springframework.stereotype.Service;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.SiteMetaData;
/**
 * CAREEM NETWORKS job site parsing class. <br>
 * URL: https://boards.greenhouse.io/careem
 * 
 * @author assaduzzaman.sohan
 * @author kamrul.islam
 * @since 2019-03-12
 */
@Service
public class CareemNewworks extends AbstractGreenHouse {
	private static final String SITE = ShortName.CAREEM_NETWORKS;

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
		//super.addBaseUrl = true;
		this.baseUrl = site.getUrl().substring(0, 28);
	}
}
