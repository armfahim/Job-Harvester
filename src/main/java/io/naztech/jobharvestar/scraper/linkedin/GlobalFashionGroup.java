package io.naztech.jobharvestar.scraper.linkedin;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;

/**
 * Global Fashion Group <br> 
 * URL: https://www.linkedin.com/jobs/search?locationId=OTHERS.worldwide&f_C=2366074%2C2986943%2C2614488%2C3126598%2C3198484%2C2907657%2C1682979%2C2003349%2C6672344&trk=companyTopCard_top-card-button&pageNum=0&position=1
 * 
 * @author Tanbirul Hashan
 * @since 2019-03-13
 */
@Service
public class GlobalFashionGroup extends AbstractLinkedinJobs {

	private static final String SITE = ShortName.GLOBAL_FASHION_GROUP;
	
	@Override
	protected Job getJobDetails(String jobUrl) throws StaleElementReferenceException, InterruptedException {
		Job job=super.getJobDetails(jobUrl);
		try{
			job.setComment(driver.findElementByCssSelector("a[data-control-name='company_link']").getText().trim());
		}catch (NoSuchElementException e) {
			log.warn("Company name xPath not found ", e);
		}
		return job;
	}
	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return null;
	}
}
