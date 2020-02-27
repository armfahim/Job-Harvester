package io.naztech.jobharvestar.scraper.taleo;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Texus Instruments job site parser. <br>
 * URL: https://ti.taleo.net/careersection/ti_ex_campus/jobsearch.ftl?lang=en&portal=101430233&ignoreSavedQuery#
 * 
 * @author naym.hossain
 * @author tanmoy.tushar
 * @since 2019-03-06
 */
@Service
public class TexusInstruments extends AbstractTaleo {
	private static final String SITE = ShortName.TEXAS_INSTRUMENTS;

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getSpecId() {
		return "requisitionDescriptionInterface.ID1547.row1";
	}
	
	@Override
	protected String getPrerequisiteId() {
		return "requisitionDescriptionInterface.ID1599.row1";
	}

	@Override
	protected String getLocationId() {
		return "requisitionDescriptionInterface.ID1708.row1";
	}

	@Override
	protected String getCategoryId() {
		return "requisitionDescriptionInterface.ID1920.row1";
	}

	@Override
	protected String getJobTypeId() {
		return null;
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
