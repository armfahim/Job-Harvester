package io.naztech.jobharvestar.scraper.icims;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Arch Capital Group <br>
 * URL: https://careers-arch.icims.com/jobs/search
 * 
 * @author tohedul.islum
 * @since 2019-02-17
 */
@Service
public class ArchCapitalGroup extends AbstractIcims {
	private static final String SITE = ShortName.ARCH_CAPITAL_GROUP;
	
	@Override
	public String getSiteName() {
		return SITE;
	}
}
