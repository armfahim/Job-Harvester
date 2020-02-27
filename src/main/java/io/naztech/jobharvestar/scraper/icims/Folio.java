package io.naztech.jobharvestar.scraper.icims;

import org.springframework.stereotype.Service;
import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Folio job site scraper. <br>
 * URL: https://careers-foliofn.icims.com/jobs/search?ss=1&hashed=-435620354
 * 
 * @author Asadullah Galib
 * @since 2019-04-01
 */
@Service
public class Folio extends AbstractIcims {
	private static final String SITE = ShortName.FOLIO;

	@Override
	public String getSiteName() {
		return SITE;
	}
}
