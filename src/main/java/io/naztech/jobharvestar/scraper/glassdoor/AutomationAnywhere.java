package io.naztech.jobharvestar.scraper.glassdoor;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
/**
 * Automation Anywhere job parsing class<br>
 * URL: https://www.glassdoor.com/Jobs/Automation-Anywhere-Jobs-E555068.htm
 * 
 * @author BM Al-Amin
 * @since 2019-03-13
 */
@Service
public class AutomationAnywhere  extends AbstractGlassDoor {
	private static final String SITE = ShortName.AUTOMATION_ANYWHERE;

	@Override
	public String getSiteName() {
		return SITE;
	}
}
