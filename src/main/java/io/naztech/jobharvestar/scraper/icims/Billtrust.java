package io.naztech.jobharvestar.scraper.icims;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.jobharvestar.scraper.icims.AbstractIcims;

/**
 * Bill Trust URL:
 * https://careers-billtrust.icims.com/jobs/search
 * 
 * @author rafayet.hossain
 * @author iftekar.alam
 * @since 2019-03-31
 */
@Service
public class Billtrust extends AbstractIcims {
	private static final String SITE = ShortName.BILLTRUST;

	@Override
	public String getSiteName() {
		return SITE;
	}
}
