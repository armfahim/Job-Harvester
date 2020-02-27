package io.naztech.jobharvestar.scraper.taleo;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Bank Of Singapore.<br>
 * URL: https://ocbc.taleo.net/careersection/bank+of+singapore_external+job+1st+submission/jobsearch.ftl
 * 
 * @author naym.hossain
 * @author rahat.ahmad
 * @author bm.alamin
 * @author tanmoy.tushar
 * @since 2019-02-24
 */
@Service
public class BankOfSingapore extends AbstractTaleo {
	private static final String SITE = ShortName.BANK_OF_SINGAPORE;
	
	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getSpecId() {
		return "requisitionDescriptionInterface.ID1556.row1";
	}

	@Override
	protected String getPrerequisiteId() {
		return "requisitionDescriptionInterface.ID1614.row1";
	}

	@Override
	protected String getLocationId() {
		return "requisitionDescriptionInterface.ID1715.row1";
	}

	@Override
	protected String getCategoryId() {
		return "requisitionDescriptionInterface.ID1927.row1";
	}

	@Override
	protected String getJobTypeId() {
		return "requisitionDescriptionInterface.ID2133.row1";
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
