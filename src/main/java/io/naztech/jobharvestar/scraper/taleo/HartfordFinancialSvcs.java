package io.naztech.jobharvestar.scraper.taleo;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * HARTFORD FINANCIAL SVCS <br>
 * URL: https://thehartford.taleo.net/careersection/2/jobsearch.ftl
 * 
 * @author Tanbirul Hashan
 * @author tanmoy.tushar
 * @since 2019-03-03
 */
@Service
public class HartfordFinancialSvcs extends AbstractTaleo {
	private static final String SITE = ShortName.HARTFORD_FINANCIAL_SVCS;

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getSpecId() {
		return "requisitionDescriptionInterface.ID1606.row1";
	}
	
	@Override
	protected String getPrerequisiteId() {
		return "requisitionDescriptionInterface.ID1662.row1";
	}

	@Override
	protected String getLocationId() {
		return "requisitionDescriptionInterface.ID1787.row1";
	}

	@Override
	protected String getCategoryId() {
		return "requisitionDescriptionInterface.ID1733.row1";
	}

	@Override
	protected String getJobTypeId() {
		return "requisitionDescriptionInterface.ID1895.row1";
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
