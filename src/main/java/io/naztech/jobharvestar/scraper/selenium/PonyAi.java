package io.naztech.jobharvestar.scraper.selenium;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.jobharvestar.scraper.AbstractWorkable;

/**
 * Pony Ai job site parser. <br>
 * URL: https://www.pony.ai/careers/
 * 
 * @author tohedul.islum
 * @author tanmoy.tushar
 * @author iftekar.alam
 *  @since 2019-03-13
 */
@Service
public class PonyAi extends AbstractWorkable {
	private static final String SITE = ShortName.PONYAI;

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return null;
	}

}
