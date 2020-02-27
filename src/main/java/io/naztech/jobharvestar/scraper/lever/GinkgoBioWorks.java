package io.naztech.jobharvestar.scraper.lever;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Ginkgo BioWorks <br>
 * URL: https://jobs.lever.co/ginkgobioworks
 * 
 * @author tohedul.islum
 * @since 2019-03-12
 */
@Service
public class GinkgoBioWorks extends AbstractLever {
	private static final String SITE = ShortName.GINKGO_BIOWORKS;

	@Override
	public String getSiteName() {
		return SITE;
	}
}