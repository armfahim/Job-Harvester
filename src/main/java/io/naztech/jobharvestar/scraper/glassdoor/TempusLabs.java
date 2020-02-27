package io.naztech.jobharvestar.scraper.glassdoor;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
/**
 * Tempus Labs job parsing class<br>
 * URL: https://www.glassdoor.com/Jobs/Tempus-Labs-Jobs-E1499366.htm
 * 
 * @author BM Al-Amin
 * @since 2019-03-14
 */
@Service
public class TempusLabs  extends AbstractGlassDoor {
	private static final String SITE = ShortName.TEMPUS_LABS;

	@Override
	public String getSiteName() {
		return SITE;
	}
}