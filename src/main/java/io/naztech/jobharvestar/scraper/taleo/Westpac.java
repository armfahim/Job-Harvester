package io.naztech.jobharvestar.scraper.taleo;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Westpac Banking <br>
 * URL: https://westpac.taleo.net/careersection/westpacgroup/jobsearch.ftl#
 * 
 * @author naym.hossain
 * @author Tanbirul Hashan
 * @author tanmoy.tushar
 * @since 2019-02-20
 */
@Service
public class Westpac extends AbstractTaleo {	
	private static final String SITE = ShortName.WESTPAC;

	@Override
	public String getSiteName() {
		return SITE;
	}
	
	@Override
	protected String getJobListPath() {
		return "//div[@class='multiline-data-container']//a";
	}

	@Override
	protected String getSpecId() {
		return "requisitionDescriptionInterface.ID1468.row1";
	}

	@Override
	protected String getPrerequisiteId() {
		return null;
	}

	@Override
	protected String getLocationId() {
		return "requisitionDescriptionInterface.ID1651.row1";
	}

	@Override
	protected String getCategoryId() {
		return "requisitionDescriptionInterface.ID1597.row1";
	}

	@Override
	protected String getJobTypeId() {
		return "requisitionDescriptionInterface.ID1813.row1";
	}

	@Override
	protected boolean hasPostedDate() {
		return true;
	}

	@Override
	protected boolean hasRefId() {
		return true;
	}

	@Override
	protected boolean hasDeadline() {
		return true;
	}

	@Override
	protected boolean hasFirstPagePostedDate() {
		return false;
	}

	@Override
	protected boolean hasFirstPageDeadline() {
		return false;
	}
	
}
