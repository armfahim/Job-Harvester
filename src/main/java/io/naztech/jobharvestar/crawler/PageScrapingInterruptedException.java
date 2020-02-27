package io.naztech.jobharvestar.crawler;

import io.naztech.jobharvestar.scraper.AbstractScraper;

/**
 * Thrown when a scraper thread is working and it was interrupted, 
 * either before or during the activity. <br>
 * Throw this exception when scraper has received the stop signal.
 * 
 * @author Imtiaz Rahi
 * @since 2019-02-18
 * @see AbstractScraper#isStopped()
 */
public class PageScrapingInterruptedException extends InterruptedException {
	private static final long serialVersionUID = 2180853478454929831L;
	private static final String SITE_MSG = "Scraper was stopped by user intervention";

	public PageScrapingInterruptedException() {
		super(SITE_MSG);
	}

	public PageScrapingInterruptedException(String s) {
		super(s);
	}

}
