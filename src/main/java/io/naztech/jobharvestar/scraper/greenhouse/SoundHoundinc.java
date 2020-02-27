package io.naztech.jobharvestar.scraper.greenhouse;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.SiteMetaData;
/**
 * Sound Houndinc job site parsing class. <br>
 * URL: https://boards.greenhouse.io/soundhoundinc
 * 
 * @author assaduzzaman.sohan
 * @author kamrul.islam
 * @since 2019-03-13
 */
@Service
public class SoundHoundinc extends AbstractGreenHouse {
	private static final String SITE = ShortName.SOUNDHOUND;
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
