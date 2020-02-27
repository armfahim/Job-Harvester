package io.naztech.jobharvestar.scraper.icims;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Berkley WR Corp <br>
 * URL: https://careers-berkley.icims.com/jobs/search
 * 
 * @author tohedul.islum
 * @since 2019-02-14
 */
@Service
public class BerkleyWrCorp extends AbstractIcims {
	private static final String SITE = ShortName.BERKLEY_WR_CORP;
	
	@Override
	public String getSiteName() {
		return SITE;
	}
}
