package io.naztech.jobharvestar.scraper.glassdoor;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
/**
 * Kik Interactive job parsing class<br>
 * URL: https://www.glassdoor.com/Jobs/Kik-Jobs-E673773.htm
 * 
 * @author BM Al-Amin
 * @since 2019-03-13
 */
@Service
public class KikInteractive  extends AbstractGlassDoor {
	private static final String SITE = ShortName.KIK_INTERACTIVE;

	@Override
	public String getSiteName() {
		return SITE;
	}
}
