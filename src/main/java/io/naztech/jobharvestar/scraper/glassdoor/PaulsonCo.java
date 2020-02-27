package io.naztech.jobharvestar.scraper.glassdoor;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Paloma Partners job parsing class<br>
 * URL: https://www.glassdoor.com/Jobs/Paulson-Investment-Company-Jobs-E981851.htm
 * 
 * @author Armaan Choudhury
 * @since: 2019-03-11
 */
@Service
public class PaulsonCo extends AbstractGlassDoor{
	private static final String SITE = ShortName.PAULSON_N_CO;
	
	@Override
	public String getSiteName() {
		return SITE;
	}
}
