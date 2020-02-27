package io.naztech.jobharvestar.scraper.taleo;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Cincinnati Financial Corp <br>
 * URL: https://cinfin.taleo.net/careersection/ex/jobsearch.ftl?lang=en
 * 
 * @author Tanbirul Hashan
 * @author tanmoy.tushar
 * @since 2019-03-03
 */
@Service
public class CincinnatiFinancialCorp extends AbstractTaleo {
	private static final String SITE = ShortName.CINCINNATI_FINL_CORP;

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getSpecId() {
		return "requisitionDescriptionInterface.ID1498.row1";
	}
	
	@Override
	protected String getPrerequisiteId() {
		return "requisitionDescriptionInterface.ID1556.row1";
	}

	@Override
	protected String getLocationId() {
		return "requisitionDescriptionInterface.ID1485.row1";
	}

	@Override
	protected String getCategoryId() {
		return null;
	}

	@Override
	protected String getJobTypeId() {
		return null;
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
