package io.naztech.jobharvestar.scraper.glassdoor;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
/**
 * WalkMe job parsing class<br>
 * URL: https://www.glassdoor.com/Jobs/WalkMe-Jobs-E699721.htm
 * 
 * @author BM Al-Amin
 * @since 2019-03-13
 */
@Service
public class WalkMe  extends AbstractGlassDoor {
	private static final String SITE = ShortName.WALKME;

	@Override
	public String getSiteName() {
		return SITE;
	}
}
