package io.naztech.jobharvestar.scraper.glassdoor;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
/**
 * Celonis job parsing class<br>
 * URL: https://www.glassdoor.com/Jobs/Celonis-Jobs-E1307503.htm
 * 
 * @author BM Al-Amin
 * @since 2019-03-13
 */
@Service
public class Celonis  extends AbstractGlassDoor {
	private static final String SITE = ShortName.CELONIS;

	@Override
	public String getSiteName() {
		return SITE;
	}
}
