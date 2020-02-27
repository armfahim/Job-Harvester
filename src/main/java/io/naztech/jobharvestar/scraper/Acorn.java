package io.naztech.jobharvestar.scraper;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.jobharvestar.scraper.lever.AbstractLever;

/**
 * Acorn jobsite pareser<br>
 * URL: https://jobs.lever.co/oaknorth.ai
 * 
 * @author Rahat Ahmad
 * @author fahim.reza
 * @since 2019-03-12
 */
@Service
public class Acorn extends AbstractLever {

	private static final String SITE = ShortName.ACORN_OAKNORTH;

	@Override
	public String getSiteName() {
		return SITE;
	}

}
