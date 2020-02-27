package io.naztech.jobharvestar.scraper.greenhouse;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
/**
 * Gusto job site parsing class. <br>
 * URL: https://boards.greenhouse.io/gusto
 * @author assaduzzaman.sohan
 * @author kamrul.islam
 * @since 2019-03-13
 */
@Service
public class Gusto extends AbstractGreenHouse {
	private static final String SITE = ShortName.GUSTO;

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
		this.baseUrl = site.getUrl().substring(0, 17);
	}
}
