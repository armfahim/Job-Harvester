package io.naztech.jobharvestar.scraper.glassdoor;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * King Street Capital job parsing class<br>
 * URL: https://www.glassdoor.com/Jobs/King-Street-Capital-Management-Jobs-E305637.htm
 * 
 * @author Armaan Choudhury
 * @since: 2019-03-11
 */
@Service
public class KingStreetCapital extends AbstractGlassDoor{
	private static final String SITE = ShortName.KING_STREET_CAPITAL_MANAGEMENT;
	
	@Override
	public String getSiteName() {
		return SITE;
	}
}
