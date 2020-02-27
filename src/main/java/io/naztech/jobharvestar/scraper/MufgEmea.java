package io.naztech.jobharvestar.scraper;

import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;

/**
 * Mufg Bank Emea<br>
 * URL: https://career5.successfactors.eu/career?company=MUSI&career_ns=job_listing_summary&navBarLevel=JOB_SEARCH&site=VjItUnUyR1hvaHNudXZSOFFoaDZWYUtYQT09&_s.crb=C%2fFw2yn82kqwKBIUmTjcFVP1tiY%3d
 * 
 * @author naym.hossain 
 * @author rahat.ahmad
 * @since 2019-02-24
 */
@Service
public class MufgEmea extends AbstractSuccessfactors {
	private static final String SITE = ShortName.MUFG_BANK_EMEA;
	private String baseUrl;

	@Override
	protected void setBaseUrl(SiteMetaData site) {
		this.baseUrl = site.getUrl().substring(0, 33);

	}
	
	@Override
	protected Job getLocationAndCategory(Elements jobInfo, Job job) {
		job.setCategory(jobInfo.get(2).text().trim());
		job.setLocation(jobInfo.get(3).text().trim());
		return job;
	}

	@Override
	protected String getNextAnchorId() {
		return "45:_next";
	}

	@Override
	public String getSiteName() {
		return SITE;
	}


	@Override
	protected String getBaseUrl() {
		return this.baseUrl;
	}
}
