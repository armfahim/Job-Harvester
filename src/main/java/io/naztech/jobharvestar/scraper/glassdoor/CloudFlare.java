package io.naztech.jobharvestar.scraper.glassdoor;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
/**
 * CloudFlare job parsing class<br>
 * URL: https://www.glassdoor.com/Jobs/Cloudflare-Jobs-E430862.htm
 * 
 * @author BM Al-Amin
 * @since 2019-03-13
 */
@Service
public class CloudFlare  extends AbstractGlassDoor {
	private static final String SITE = ShortName.CLOUDFLARE;

	@Override
	public String getSiteName() {
		return SITE;
	}
}
