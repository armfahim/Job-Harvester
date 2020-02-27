package io.naztech.jobharvestar.scraper.icims;

import org.springframework.stereotype.Service;
import io.naztech.jobharvestar.crawler.ShortName;

/**
 * First Republic Bank <br>
 * URL: https://careers-firstrepublic.icims.com/jobs/search?pr=0
 * 
 * @author Armaan Seraj Choudhury(Refactored by tohedul islum)
 * @author rahat.ahmad
 * @since 2019-02-27
 */
@Service
public class FirstRepublic extends AbstractIcims {
	private static final String SITE = ShortName.FIRST_REPUBLIC_BANK;

	@Override
	public String getSiteName() {
		return SITE;
	}
}
