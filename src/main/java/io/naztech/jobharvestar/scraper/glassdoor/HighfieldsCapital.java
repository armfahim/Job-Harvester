package io.naztech.jobharvestar.scraper.glassdoor;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Highfields Capital job parsing class<br>
 * URL: https://www.glassdoor.com/Jobs/Highfields-Capital-Management-Jobs-E1078081.htm
 * 
 * @author Armaan Choudhury
 * @since: 2019-03-10
 */
@Service
public class HighfieldsCapital extends AbstractGlassDoor{
	private static final String SITE = ShortName.HIGHFIELDS_CAPITAL_MANAGEMENT;
	
	@Override
	public String getSiteName() {
		return SITE;
	}
}
