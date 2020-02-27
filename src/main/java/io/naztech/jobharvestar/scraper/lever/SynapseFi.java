package io.naztech.jobharvestar.scraper.lever;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.jobharvestar.scraper.lever.AbstractLever;

/**
 * SynapseFi  Job parse  <br>
 * URL: https://jobs.lever.co/synapsefi
 * 
 * @author jannatul.maowa
 * @author iftekar.alam
 * @since 2019-03-31
 */
@Service
public class SynapseFi extends AbstractLever{

	private static final String SITE = ShortName.SYNAPSEFI;

	@Override
	public String getSiteName() {
		return SITE;
	}
}
