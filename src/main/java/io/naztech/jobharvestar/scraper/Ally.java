package io.naztech.jobharvestar.scraper;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;

/**
 * Ally Financial jobs site parser <br>
 * URL: https://recruiting.adp.com/srccar/public/RTI.home?d=AllyCareers&c=1125607
 * 
 * @author Rahat Ahmad
 * @author tanmoy.tushar
 * @since 2019-02-24
 * 
 */
@Service
public class Ally extends AbstractRecruitingAdp {

	private static final String SITE = ShortName.ALLY_FINANCIAL;
	private String baseUrl;

	@Override
	protected Job getJobDetails(WebElement jobE, Job job) {
		try {
			job.setReferenceId(jobE.findElement(By.xpath("//div[@id='field2_right']/div[2]")).getText());
			job.setCategory(jobE.findElement(By.xpath("//div[@id='field1_left']/div[2]")).getText());
			job.setLocation(jobE.findElement(By.xpath("//div[@id='field2_left']/div[2]")).getText());
		} catch (NoSuchElementException e) {
			/* Intentionally blank */
		}
		job.setSpec(jobE.findElement(By.xpath("//div[@id='field3_left']/div[2]")).getText());
		job.setUrl(getJobHash(job));
		return job;
	}

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return baseUrl;
	}	
}