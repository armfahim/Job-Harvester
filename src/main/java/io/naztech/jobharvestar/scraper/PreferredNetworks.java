package io.naztech.jobharvestar.scraper;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Preferred Networks<br>
 * URL: https://apply.workable.com/preferred-networks/
 * 
 * @author Armaan Seraj Choudhury
 * @author tanmoy.tushar
 * @author bm.alamin
 * @since 2019-03-13
 */
@Service
public class PreferredNetworks extends AbstractWorkable {
	private static final String SITE = ShortName.PREFERRED_NETWORKS;

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return null;
	}
}
