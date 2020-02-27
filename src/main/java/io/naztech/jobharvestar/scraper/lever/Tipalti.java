package io.naztech.jobharvestar.scraper.lever;
import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.jobharvestar.scraper.lever.AbstractLever;

/**
 * Tipalti Job site Parser.<br>
 * URL: https://jobs.lever.co/tipalti/
 * 
 * @author fahim.reza
 * @author iftekar.alam
 * @since 2019-03-27
 */
@Service
public class Tipalti extends AbstractLever {
	private static final String SITE = ShortName.TIPALTI;

	@Override
	public String getSiteName() {
		return SITE;
	}
	
}
