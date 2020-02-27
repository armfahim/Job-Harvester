package io.naztech.jobharvestar.scraper.taleo;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Standard Life Aberdeen <br>
 * URL: https://standardlife.taleo.net/careersection/global+sl+external+career+site+eng/jobsearch.ftl
 * 
 * @author Nuzhat Tabassum
 * @author Tanbirul Hashan
 * @author tanmoy.tushar
 * @since 2019-03-03
 */
@Service
public class StandardLifeAberdeen extends AbstractTaleo {
	private static final String SITE = ShortName.STANDARD_LIFE_ABERDEEN;

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getSpecId() {
		return "requisitionDescriptionInterface.ID1613.row1";
	}
	
	@Override
	protected String getPrerequisiteId() {
		return "requisitionDescriptionInterface.ID1669.row1";
	}

	@Override
	protected String getLocationId() {
		return "requisitionDescriptionInterface.ID1828.row1";
	}

	@Override
	protected String getCategoryId() {
		return "requisitionDescriptionInterface.ID1772.row1";
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean hasFirstPageDeadline() {
		// TODO Auto-generated method stub
		return false;
	}
}
