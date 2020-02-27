package io.naztech.jobharvestar.scraper;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.jobharvestar.scraper.AbstractBreezyHr;

/**
 * Sigfig job site parsing class. <br>
 * URL: https://sigfig.breezy.hr/
 * 
 * @author muhammad.tarek
 * @author kamrul.islam
 * @since 2019-04-01
 */
@Service
public class Sigfig extends AbstractBreezyHr {
	private static final String SITE = ShortName.SIGFIG;

	@Override
	public String getSiteName() {
		return SITE;
	}
}
