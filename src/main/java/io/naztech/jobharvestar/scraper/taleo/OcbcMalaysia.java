package io.naztech.jobharvestar.scraper.taleo;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * OCBC MALAYSIA job site parser. <br>
 * URL: https://ocbc.taleo.net/careersection/ocbc_my_external+1st+subm/jobsearch.ftl?lang=en&%23
 * 
 * @author Mahmud Rana
 * @author Tanbirul Hashan
 * @author tanmoy.tushar
 * @since 2019-02-25
 */
@Service
public class OcbcMalaysia extends AbstractTaleo {
	private static final String SITE = ShortName.OCBC_MALAYSIA;

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getSpecId() {
		return "requisitionDescriptionInterface.ID1480.row1";
	}
	
	@Override
	protected String getPrerequisiteId() {
		return "requisitionDescriptionInterface.ID1538.row1";
	}

	@Override
	protected String getLocationId() {
		return "requisitionDescriptionInterface.ID1639.row1";
	}

	@Override
	protected String getCategoryId() {
		return "requisitionDescriptionInterface.ID1851.row1";
	}

	@Override
	protected String getJobTypeId() {
		return "requisitionDescriptionInterface.ID2057.row1";
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
