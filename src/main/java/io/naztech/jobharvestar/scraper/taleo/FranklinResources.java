package io.naztech.jobharvestar.scraper.taleo;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Franklin Resources <br>
 * URL: https://fti.taleo.net/careersection/2/jobsearch.ftl
 * 
 * @author Tanbirul Hashan
 * @author tanmoy.tushar
 * @since 2019-03-03
 */
@Service
public class FranklinResources extends AbstractTaleo {
	private static final String SITE = ShortName.FRANKLIN_RESOURCES;

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getSpecId() {
		return "requisitionDescriptionInterface.ID1493.row1";
	}
	
	@Override
	protected String getPrerequisiteId() {
		return "requisitionDescriptionInterface.ID1549.row1";
	}

	@Override
	protected String getLocationId() {
		return "requisitionDescriptionInterface.ID1659.row1";
	}

	@Override
	protected String getCategoryId() {
		return "requisitionDescriptionInterface.ID1648.row1";
	}

	@Override
	protected String getJobTypeId() {
		return "requisitionDescriptionInterface.ID1767.row1";
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
