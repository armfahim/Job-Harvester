package io.naztech.jobharvestar.scraper.glassdoor;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Solus Alternative Asset Management job parsing class<br>
 * URL: https://www.glassdoor.co.in/Jobs/Solus-Alternative-Asset-Management-Jobs-E285601.htm
 * 
 * @author Armaan Choudhury
 * @since: 2019-03-11
 */
@Service
public class SolusAlternative extends AbstractGlassDoor{
	private static final String SITE = ShortName.SOLUS_ALTERNATIVE_ASSET_MANAGEMENT;
	
	@Override
	public String getSiteName() {
		return SITE;
	}
}
