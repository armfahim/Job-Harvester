package io.naztech.jobharvestar.scraper.taleo;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * PROGRESSIVE CORP <br>
 * URL: https://progressive.taleo.net/careersection/2/jobsearch.ftl?lang=en
 * 
 * @author Mahmud Rana
 * @author Tanbirul Hashan
 * @author tanmoy.tushar
 * @author fahim.reza
 * @since 2019-02-25
 */
@Service
public class ProgressiveCorp extends AbstractTaleo {
	private static final String SITE = ShortName.PROGRESSIVE_CORP;

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
		return null;
	}

	@Override
	protected String getLocationId() {
		return "requisitionDescriptionInterface.ID1679.row1";
	}

	@Override
	protected String getCategoryId() {
		return "requisitionDescriptionInterface.ID1621.row1";
	}

	@Override
	protected String getJobTypeId() {
		return "requisitionDescriptionInterface.ID1795.row1";
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
		// TODO Auto-generated method stub
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
