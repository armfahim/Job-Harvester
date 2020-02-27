package io.naztech.jobharvestar.scraper.glassdoor;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Pershing Square Capital job parsing class<br>
 * URL: https://www.glassdoor.com/Jobs/Pershing-Square-Holdings-Jobs-E1401746.htm
 * 
 * @author Armaan Choudhury
 * @since: 2019-03-11
 */
@Service
public class PershingSquare extends AbstractGlassDoor{
	private static final String SITE = ShortName.PERSHING_SQUARE_CAPITAL_MANAGEMENT;
	
	@Override
	public String getSiteName() {
		return SITE;
	}
}
