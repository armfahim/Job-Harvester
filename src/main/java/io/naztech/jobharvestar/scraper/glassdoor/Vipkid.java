package io.naztech.jobharvestar.scraper.glassdoor;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
/**
 * Vipkid job parsing class<br>
 * URL: https://www.glassdoor.com/Jobs/VIPKID-Jobs-E1105111.htm?filter.fromAge=7
 * 
 * THERE ARE 12300 JOBS AVAILABLE. BUT NO WAY TO LOAD MORE THAN 11 or AVAILABLE JOBS.
 * @author BM Al-Amin
 * @since 2019-03-14
 */
@Service
public class Vipkid  extends AbstractGlassDoor {
	private static final String SITE = ShortName.VIPKID;

	@Override
	public String getSiteName() {
		return SITE;
	}
}
