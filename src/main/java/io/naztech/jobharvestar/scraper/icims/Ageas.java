package io.naztech.jobharvestar.scraper.icims;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * AgeasCapital Group <br>
 * URL: https://careers-ageas-uk.icims.com/jobs/search?mobile=false&width=970&height=500&bga=true&needsRedirect=false&jan1offset=360&jun1offset=360
 * 
 * @author farzana.islam
 * @since 2019-02-19
 */
@Service
public class Ageas extends AbstractIcims {
	private static final String SITE = ShortName.AGEAS;

	@Override
	public String getSiteName() {
		return SITE;
	}
}
