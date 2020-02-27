package io.naztech.jobharvestar.scraper.glassdoor;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
/**
 * Oscar Health job site parsing class<br>
 * url: https://www.glassdoor.com/Jobs/Oscar-Health-Jobs-E812257.htm
 * 
 * @author bm.alamin
 *
 * Since: 2019-02-04
 */
@Service
public class OscarHealth extends AbstractGlassDoor {
	private static final String SITE = ShortName.OSCAR_HEALTH;
	
	@Override
	public String getSiteName() {
		return SITE;
	}
}