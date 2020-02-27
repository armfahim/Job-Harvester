package io.naztech.jobharvestar.scraper.glassdoor;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Goldman Sachs Asset Management job parsing class<br>
 * URL: https://www.glassdoor.com/Jobs/Goldman-Sachs-asset-management-analyst-Jobs-EI_IE2800.0,13_KO14,38.htm
 * 
 * @author Armaan Seraj Choudhury
 * @since 2019-03-11
 */
@Service
public class GoldmanSachsAssetMgmt extends AbstractGlassDoor {
	
	private static final String SITE = ShortName.GOLDMAN_SACHS_ASSET_MANAGEMENT;
	
	@Override
	public String getSiteName() {
		return SITE;
	}
}
