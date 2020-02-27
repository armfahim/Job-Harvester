package io.naztech.jobharvestar.scraper.icims;

import org.springframework.stereotype.Service;
import io.naztech.jobharvestar.crawler.ShortName;

/**
 * EastWestBanCorp <br>
 * URL: https://careers-eastwestbank.icims.com/jobs/search?mobile=false&width=716&height=500&bga=true&needsRedirect=false&jan1offset=360&jun1offset=360
 * 
 * @author farzana.islam
 * @author tanmoy.tushar
 * @since 2019-02-20
 */
@Service
public class EastWestBanCorp extends AbstractIcims {
	private static final String SITE = ShortName.EAST_WEST_BANCORP;

	@Override
	public String getSiteName() {
		return SITE;
	}
}
