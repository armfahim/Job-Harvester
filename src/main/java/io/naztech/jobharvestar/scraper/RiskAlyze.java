package io.naztech.jobharvestar.scraper;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * RiskAlyze jobsite<br>
 * URL: https://apply.workable.com/riskalyze/
 * 
 * @author Muhammad Bin Farook
 * @author jannatul.maowa
 * @author iftekar.alam
 * @since 2019-03-27
 */
@Service
public class RiskAlyze extends AbstractWorkable {
	private static final String SITE = ShortName.RISKALYZE;

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return null;
	}
	
}
