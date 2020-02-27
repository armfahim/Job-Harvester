package io.naztech.jobharvestar.scraper;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.jobharvestar.scraper.AbstractWorkable;

/**
 * The Hut Group job site parsing class. <br>
 * URL: https://apply.workable.com/thehutgroup/
 * 
 * @author Shajedul Islam
 * @author tanmoy.tushar
 * @author iftekar.alam
 * @author bm.alamin
 * @since 2019-04-16
 */
@Service
public class TheHutGroup extends AbstractWorkable {

	private static final String SITE = ShortName.THE_HUT_GROUP;

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return null;
	}


}
