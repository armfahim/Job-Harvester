package io.naztech.jobharvestar.scraper;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * Bitflyre job site scrapper<br>
 * URL: https://bitflyer.com/en-jp/recruit
 * 
 * @author Alif Choyon
 * @since 2019-04-1
 */
@Slf4j
@Service
public class Bitflyre extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.BITFLYRE;
	private static String SITE_URL = null;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(getSiteName()));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		getSummaryPages(site);
	}

	public void getSummaryPages(SiteMetaData site) throws PageScrapingInterruptedException, IOException {
		try {
			SITE_URL = site.getUrl();
			Document doc = Jsoup.connect(SITE_URL).get();
			Elements titleListE = doc.select("li[class=positions__item]");
			expectedJobCount = titleListE.size();
			for (Element element : titleListE) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				try {
				saveJob(getJobDetails(element), site);
				}catch(Exception e) {
					exception = e;
				}
			}
		} catch (IOException e) {
			log.warn("Failed to parse job details" + site.getUrl(), e);
			throw e;
		}
	}

	public Job getJobDetails(Element e) throws PageScrapingInterruptedException {
		String baseUrl = getBaseUrl();
		String url = baseUrl + e.select("a").first().attr("href");
		Job j = new Job();
		try {
			Document doc = Jsoup.connect(url).get();
			Element jobSec = doc.select("#top > main > section.section.offer-content").first();
			j.setTitle(e.text());
			j.setName(j.getTitle());
			j.setUrl(url);
			j.setApplicationUrl(baseUrl
					+ jobSec.select("div.offer-content__entry-button-wrapper > a.recruit-btn").first().attr("href"));

			String spec = "";
			for (Element element : jobSec.select("div[class=offer-content__group]")) {
				if (isStopped())
					throw new PageScrapingInterruptedException();
				if (element.select("h1").first().text().toLowerCase().contains("job location")) {
					j.setLocation(element.text());
				} else {
					spec += element.text();
				}

			}
			j.setSpec(spec);
		} catch (IOException ioe) {
			log.warn("Failed to parse job details" + j.getUrl(), ioe);
		}
		return j;
	}

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return SITE_URL.substring(0, 26);
	}
	
	@Override
	protected int getExpectedJob() {
		return expectedJobCount;
	}
	
	@Override
	protected void destroy() {
	}

	@Override
	protected Exception getFailedException() {
		return exception;
	}
}