package io.naztech.jobharvestar.scraper.taleo;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Oracle Corporations <br>
 * URL: https://oracle.taleo.net/careersection/2/jobsearch.ftl?lang=en
 * 
 * @author naym.hossain
 * @author tanmoy.tushar
 * @since 6-3-2019
 */
@Service
public class OracleCorporation extends AbstractTaleo {
	private static final String SITE = ShortName.ORACLE_CORPORATION;

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getSpecId() {
		return "requisitionDescriptionInterface.ID1590.row1";
	}
	
	@Override
	protected String getPrerequisiteId() {
		return "requisitionDescriptionInterface.ID1532.row1";
	}

	@Override
	protected String getLocationId() {
		return "requisitionDescriptionInterface.ID1791.row1";
	}

	@Override
	protected String getCategoryId() {
		return "requisitionDescriptionInterface.ID1691.row1";
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
	protected String getJobListPath() {
		return "//div[@class='multiline-data-container']//a";
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
