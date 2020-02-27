package io.naztech.jobharvestar.scraper.glassdoor;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.jobharvestar.scraper.glassdoor.AbstractGlassDoor;

/**
 * True value Labs parsing site. <br>
 * URL: https://www.glassdoor.com/Jobs/TruValue-Labs-Jobs-E1328300.htm
 * 
 * @author sanowar.ali
 * @author bm.alamin
 * @since 2019-05-08
 */
@Service
public class TruvalueLabs extends AbstractGlassDoor {
	private static final String SITE = ShortName.TRUVALUE_LABS;

	@Override
	public String getSiteName() {
		return SITE;
	}
	
}
