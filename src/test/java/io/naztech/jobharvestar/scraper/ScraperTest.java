package io.naztech.jobharvestar.scraper;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import io.naztech.jobharvestar.crawler.Scrapper;

public class ScraperTest {

	private Scrapper scrapper;

	@Before
	public void setUp() throws Exception {

	}

	@Test
	public void test() throws IOException, InterruptedException {
		scrapper.getScrapedJobs(null);
	}

}
