package io.naztech.jobharvestar.scraper.glassdoor;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
/**
 * iTutorGroup job parsing class<br>
 * URL: https://www.glassdoor.com/Jobs/iTutorGroup-Jobs-E1045804.htm
 * 
 * @author BM Al-Amin
 * @since 2019-03-13
 */
@Service
public class ItutorGroup  extends AbstractGlassDoor {
	private static final String SITE = ShortName.ITUTORGROUP;

	@Override
	public String getSiteName() {
		return SITE;
	}
}
