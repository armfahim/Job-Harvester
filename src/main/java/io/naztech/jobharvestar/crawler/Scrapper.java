package io.naztech.jobharvestar.crawler;

import java.io.IOException;

import org.springframework.scheduling.annotation.Async;

import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;

/**
 * Web site scrapper launch interface.
 * 
 * @author Imtiaz Rahi
 * @since 2019-01-13
 */
public interface Scrapper {
	/** Scrap job information from career site after reading {@link SiteMetaData} 
	 * @throws Exception */
	@Async
	void scrapJobs() throws Exception;

	/**
	 * Scrap jobs from web site. Entry point for developers. <br>
	 * Developers should also implement a {@code getJobDetail} method which returns a complete {@link Job} instance.
	 * 
	 * @param siteMeta {@link SiteMetaData} instance
	 * @throws IOException IO Exception
	 * @throws InterruptedException Thread interrupted exception
	 */
	void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException;

	String getSiteName();

	void stopIt();
}
