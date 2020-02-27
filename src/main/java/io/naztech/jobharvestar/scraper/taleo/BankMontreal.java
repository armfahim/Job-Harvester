package io.naztech.jobharvestar.scraper.taleo;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * BANK MONTREAL <br> 
 * URL: https://bmo.taleo.net/careersection/2/jobsearch.ftl?lang=en_GB&keyword=#
 * 
 * @author tohedul.islum
 * @author Tanbirul Hashan
 * @author tanmoy.tushar
 * @since 2019-02-25
 */
@Service
public class BankMontreal extends AbstractTaleo {
	private static final String SITE = ShortName.BANK_MONTREAL;

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
		return "requisitionDescriptionInterface.ID1545.row1";
	}
	
	@Override
	protected String getPrerequisiteId() {
		return "requisitionDescriptionInterface.ID1601.row1";
	}

	@Override
	protected String getLocationId() {
		return "requisitionDescriptionInterface.ID1695.row1";
	}

	@Override
	protected String getCategoryId() {
		return "requisitionDescriptionInterface.ID1651.row1";
	}

	@Override
	protected String getJobTypeId() {
		return "requisitionDescriptionInterface.ID1823.row1";
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean hasFirstPageDeadline() {
		// TODO Auto-generated method stub
		return false;
	}
}
