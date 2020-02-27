package io.naztech.jobharvestar.scraper.glassdoor;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Odey Asset Management job parsing class<br>
 * URL: https://www.glassdoor.com/Jobs/Odey-Asset-Management-Jobs-E1051971.htm
 * 
 * @author Armaan Choudhury
 * @since: 2019-03-11
 */
@Service
public class OdeyAsset extends AbstractGlassDoor{
	private static final String SITE = ShortName.ODEY_ASSET_MANAGEMENT;
	
	@Override
	public String getSiteName() {
		return SITE;
	}
}
