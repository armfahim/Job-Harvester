package io.naztech.jobharvestar.scraper.taleo;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * XL GROUP job site parser. <br>
 * URL: https://xl.taleo.net/careersection/001xlcatlinexternalcareersection/jobsearch.ftl?lang=en
 * 
 * @author Tanbirul Hashan
 * @author tanmoy.tushar
 * @since 2019-02-24
 */
@Service
public class XlGroup extends AbstractTaleo {
	private static final String SITE = ShortName.XL_GROUP;

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getSpecId() {
		return "requisitionDescriptionInterface.ID1602.row1";
	}
	
	@Override
	protected String getPrerequisiteId() {
		return "requisitionDescriptionInterface.ID1701.row1";
	}

	@Override
	protected String getLocationId() {
		return "requisitionDescriptionInterface.ID1773.row1";
	}

	@Override
	protected String getCategoryId() {
		return "requisitionDescriptionInterface.ID1991.row1";
	}

	@Override
	protected String getJobTypeId() {
		return "requisitionDescriptionInterface.ID2043.row1";
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
	protected String getJobListPath() {
		return "//div[@class='absolute']/span/a";
	}

	@Override
	protected boolean hasDeadline() {
		return false;
	}

	@Override
	protected boolean hasFirstPagePostedDate() {
		return true;
	}

	@Override
	protected boolean hasFirstPageDeadline() {
		return false;
	}
}
