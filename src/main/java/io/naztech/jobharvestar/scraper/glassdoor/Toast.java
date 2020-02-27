package io.naztech.jobharvestar.scraper.glassdoor;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.jobharvestar.scraper.glassdoor.AbstractGlassDoor;

/**
 * Toast job parsing class<br>
 * URL: https://careers.toasttab.com/
 * 
 * https://www.glassdoor.com/Jobs/Toast-Inc-Jobs-E989964.htm
 * 
 * @author Muhammad Bin Farook
 * @author tanmoy.tushar
 * @author iftekar.alam
 * @since 2019-04-29
 */
@Service
public class Toast extends AbstractGlassDoor {
	private static final String SITE = ShortName.TOAST;

	@Override
	public String getSiteName() {
		return SITE;
	}

}
