package io.naztech.jobharvestar.scraper.glassdoor;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
/**
 * Carbon3D job parsing class<br>
 * URL: https://www.glassdoor.com/Jobs/Carbon-Jobs-E1406305.htm
 * 
 * @author BM Al-Amin
 * @since 2019-03-13
 */
@Service
public class CarbonThreeD  extends AbstractGlassDoor {
	private static final String SITE = ShortName.CARBON3D;

	@Override
	public String getSiteName() {
		return SITE;
	}
}
