package io.naztech.jobharvestar.scraper.glassdoor;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
/**
 * Roblox job site parser<br>
 * URL: https://www.glassdoor.com/Jobs/Roblox-Jobs-E242265.htm
 * 
 * @author BM Al-Amin
 * @since 2019-03-13
 */
@Service
public class Roblox  extends AbstractGlassDoor {
	private static final String SITE = ShortName.ROBLOX;

	@Override
	public String getSiteName() {
		return SITE;
	}
}
