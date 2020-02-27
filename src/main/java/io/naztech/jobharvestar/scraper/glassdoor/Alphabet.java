
package io.naztech.jobharvestar.scraper.glassdoor;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Alphabet job parsing class<br>
 * URL: https://www.glassdoor.com/Jobs/Alphabet-Jobs-E1043369.htm
 * 
 * @author BM Al-Amin
 * @since: 2019-03-06
 */
@Service
public class Alphabet extends AbstractGlassDoor{
	private static final String SITE = ShortName.ALPHABET;
	@Override
	public String getSiteName() {
		return SITE;
	}
}

