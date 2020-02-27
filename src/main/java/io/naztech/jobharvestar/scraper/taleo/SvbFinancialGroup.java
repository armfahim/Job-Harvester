package io.naztech.jobharvestar.scraper.taleo;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * SVB FINANCIAL GROUP <br>
 * URL: https://svbank.taleo.net/careersection/ex/jobsearch.ftl
 * 
 * @author Tanbirul Hashan
 * @author tanmoy.tushar
 * @since 2019-03-03
 */
@Service
public class SvbFinancialGroup extends AbstractTaleo {
	private static final String SITE = ShortName.SVB_FINANCIAL_GROUP;

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getSpecId() {
		return "requisitionDescriptionInterface.ID1518.row1";
	}
	
	@Override
	protected String getPrerequisiteId() {
		return "requisitionDescriptionInterface.ID1576.row1";
	}

	@Override
	protected String getLocationId() {
		return "requisitionDescriptionInterface.ID1677.row1";
	}

	@Override
	protected String getCategoryId() {
		return "requisitionDescriptionInterface.ID1889.row1";
	}

	@Override
	protected String getJobTypeId() {
		return "requisitionDescriptionInterface.ID2139.row1";
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