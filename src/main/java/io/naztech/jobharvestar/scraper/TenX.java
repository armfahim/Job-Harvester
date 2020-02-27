package io.naztech.jobharvestar.scraper;


import org.springframework.stereotype.Service;
import io.naztech.jobharvestar.crawler.ShortName;
/**
 * Ten-X job site scraper.<br>
 * URL: https://apply.workable.com/tenx/j
 * 
 * @author Kayumuzzaman Robin
 * @author iftekar.alam
 * @since 2019-03-19
 */
@Service
public class TenX extends AbstractWorkable {
	private static final String SITE = ShortName.TEN_X;

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return null;
	}
}
