package io.naztech.jobharvestar.scraper.glassdoor;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Lansdowne Partners job parsing class<br>
 * URL: https://www.glassdoor.co.uk/Jobs/Lansdowne-Partners-Jobs-E305638.htm
 * 
 * @author Armaan Choudhury
 * @since: 2019-03-11
 */
@Service
public class LansdownePartners extends AbstractGlassDoor{
	private static final String SITE = ShortName.LANSDOWNE_PARTNERS_UK;
	
	@Override
	public String getSiteName() {
		return SITE;
	}
}
