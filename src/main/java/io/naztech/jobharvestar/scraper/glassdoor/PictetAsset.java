package io.naztech.jobharvestar.scraper.glassdoor;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Pictet Asset Capital Management job parsing class<br>
 * URL: https://www.glassdoor.com/Jobs/Pictet-Jobs-E348008.htm
 * 
 * @author Armaan Choudhury
 * @since: 2019-03-11
 */
@Service
public class PictetAsset extends AbstractGlassDoor{
	private static final String SITE = ShortName.PICTET_ASSET_MANAGEMENT;
	
	@Override
	public String getSiteName() {
		return SITE;
	}
}
