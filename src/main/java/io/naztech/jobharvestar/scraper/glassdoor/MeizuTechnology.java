package io.naztech.jobharvestar.scraper.glassdoor;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
/**
 * Meizu Technology job parsing class<br>
 * URL: https://www.glassdoor.com/Jobs/Meizu-Jobs-E2099172.htm
 * 
 * @author BM Al-Amin
 * @since 2019-03-13
 */
@Service
public class MeizuTechnology  extends AbstractGlassDoor {
	private static final String SITE = ShortName.MEIZU_TECHNOLOGY;

	@Override
	public String getSiteName() {
		return SITE;
	}
}
