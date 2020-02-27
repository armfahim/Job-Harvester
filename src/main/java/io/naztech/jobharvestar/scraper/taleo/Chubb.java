package io.naztech.jobharvestar.scraper.taleo;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Chubb job site parser. <br>
 * URL: https://acetalent.taleo.net/careersection/ace_external/jobsearch.ftl?lang=en#
 * 
 * @author naym.hossain
 * @author Tanbirul Hashan
 * @author tanmoy.tushar
 * @since 2019-02-26
 */
@Service
public class Chubb extends AbstractTaleo {
	private static final String SITE = ShortName.CHUBB;

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getSpecId() {
		return "requisitionDescriptionInterface.ID1542.row1";
	}
	
	@Override
	protected String getPrerequisiteId() {
		return null;
	}

	@Override
	protected String getLocationId() {
		return "requisitionDescriptionInterface.reqSiteName.row1";
	}

	@Override
	protected String getCategoryId() {
		return "requisitionDescriptionInterface.ID1943.row1";
	}

	@Override
	protected String getJobTypeId() {
		return null;
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
		return false;
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
