package io.naztech.jobharvestar.scraper.taleo;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Erste Group Bank job site parsing class. <br>
 * URL: https://sitsolutions.taleo.net/careersection/ex/jobsearch.ftl?lang=en#
 * 
 * @author tanmoy.tushar
 * @since 2019-03-04
 */
@Service
public class ErsteGroupBank extends AbstractTaleo {
	private static final String SITE = ShortName.ERSTE_GROUP_BANK;

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getSpecId() {
		return "requisitionDescriptionInterface.ID1397.row1";
	}
	
	@Override
	protected String getPrerequisiteId() {
		return null;
	}

	@Override
	protected String getLocationId() {
		return "requisitionDescriptionInterface.ID1512.row1";
	}

	@Override
	protected String getCategoryId() {
		return null;
	}

	@Override
	protected String getJobTypeId() {
		return "requisitionDescriptionInterface.ID1458.row1";
	}

	@Override
	protected boolean hasPostedDate() {
		return false;
	}
	
	@Override
	protected boolean hasRefId() {
		return false;
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