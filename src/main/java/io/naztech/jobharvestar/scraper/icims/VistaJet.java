package io.naztech.jobharvestar.scraper.icims;
import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * VistaJet <br>
 * URL: https://careers-vistajet.icims.com/jobs/search?pr=0
 * 
 * @author tohedul.islum
 * @since 2019-03-14
 */
@Service
public class VistaJet extends AbstractIcims {
	private static final String SITE = ShortName.VISTAJET;

	@Override
	public String getSiteName() {
		return SITE;
	}
}
