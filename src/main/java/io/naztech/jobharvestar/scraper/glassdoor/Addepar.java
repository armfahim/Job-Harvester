package io.naztech.jobharvestar.scraper.glassdoor;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.jobharvestar.scraper.glassdoor.AbstractGlassDoor;

/**
 * Hike <br>
 * URL: https://www.glassdoor.com/Jobs/Addepar-Jobs-E423990.htm
 * 
 * @author muhammad.tarek
 * @author bm.alamin
 * @since 2019-03-28
 */
@Service
public class Addepar extends AbstractGlassDoor {
	private static final String SITE = ShortName.ADDEPAR;

	@Override
	public String getSiteName() {
		return SITE;
	}
}
