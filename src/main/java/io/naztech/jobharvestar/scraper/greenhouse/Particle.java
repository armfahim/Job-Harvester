package io.naztech.jobharvestar.scraper.greenhouse;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.SiteMetaData;
/**
 * Particle job site parsing class. <br>
 * URL: https://boards.greenhouse.io/embed/job_board?for=particle&b=https%3A%2F%2Fwww.particle.io%2Fjobs%2F
 * 
 * @author assaduzzaman.sohan
 * @author kamrul.islam
 * @author fahim.reza
 * @since 2019-03-14
 */
@Service
public class Particle extends AbstractGreenHouse {
	private static final String SITE = ShortName.PARTICLE;

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
		this.baseUrl = site.getUrl().substring(0, 28);
	}
}
