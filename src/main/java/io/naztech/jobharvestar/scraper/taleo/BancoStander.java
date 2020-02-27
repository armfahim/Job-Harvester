package io.naztech.jobharvestar.scraper.taleo;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * BANCO STANDER <br>
 * URL: https://santander.taleo.net/careersection/career_esp/jobsearch.ftl?lang=en
 * 
 * @author Tanbirul Hashan
 * @author tanmoy.tushar
 * @since 2019-03-03
 */
@Service
public class BancoStander extends AbstractTaleo {
	private static final String SITE = ShortName.BANCO_SANTANDER;

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
		return "requisitionDescriptionInterface.ID1474.row1";
	}
	
	@Override
	protected String getPrerequisiteId() {
		return "requisitionDescriptionInterface.ID1530.row1";
	}

	@Override
	protected String getLocationId() {
		return "requisitionDescriptionInterface.ID1625.row1";
	}

	@Override
	protected String getCategoryId() {
		return "requisitionDescriptionInterface.ID1671.row1";
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
