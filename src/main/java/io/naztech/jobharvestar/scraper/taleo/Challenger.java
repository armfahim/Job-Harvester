package io.naztech.jobharvestar.scraper.taleo;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Challenger<br>
 * URL: https://challenger.taleo.net/careersection/ext/jobsearch.ftl#
 * 
 * @author Tanbirul Hashan
 * @author tanmoy.tushar
 * @since 2019-02-27
 */
@Service
public class Challenger extends AbstractTaleo {
	private static final String SITE = ShortName.CHALLENGER;

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getSpecId() {
		return "requisitionDescriptionInterface.ID1491.row1";
	}

	@Override
	protected String getPrerequisiteId() {
		return null;
	}

	@Override
	protected String getLocationId() {
		return "requisitionDescriptionInterface.ID1644.row1";
	}

	@Override
	protected String getCategoryId() {
		return null;
	}

	@Override
	protected String getJobTypeId() {
		return "requisitionDescriptionInterface.ID1598.row1";
	}

	@Override
	protected boolean hasPostedDate() {
		return false;
	}

	@Override
	protected boolean hasRefId() {
		return true;
	}
	
	@Override
	protected String getPostedDateId() {
		return "requisitionDescriptionInterface.reqUnpostingDate.row1";
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
