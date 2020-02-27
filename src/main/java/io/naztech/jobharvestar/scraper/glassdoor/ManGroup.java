package io.naztech.jobharvestar.scraper.glassdoor;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Man Group job parsing class<br>
 * URL: https://www.glassdoor.com/Jobs/Man-Group-London-Jobs-EI_IE234069.0,9_IL.10,16_IC2671300.htm
 * 
 * @author BM Al-Amin
 * @since: 2019-03-06
 */
@Service
public class ManGroup extends AbstractGlassDoor{
	private static final String SITE = ShortName.MAN_GROUP;
	@Override
	public String getSiteName() {
		return SITE;
	}
}
