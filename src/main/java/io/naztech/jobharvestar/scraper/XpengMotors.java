package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

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
 * XpengMotors jobs site parser<br>
 * URL: https://www.cardesignnews.com/careers
 * 
 * @author kowshik.saha
 * @since 2019-04-09
 */
@Slf4j
@Service
public class XpengMotors extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.XPENG_MOTORS;
	private int expectedJobCount;
	private Exception exception;
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	
	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		getSummaryPage(site);
	}

	private void getSummaryPage(SiteMetaData site) throws PageScrapingInterruptedException, IOException {
		try {
			Document doc = Jsoup.connect(site.getUrl()).get();
			Elements e = doc.selectFirst("div[class=spinNavigation gridLayout colour1]").select("ul>li");
			expectedJobCount = e.size();
			for (Element element : e) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				try {
					saveJob(getJobDetails(element), site);					
				} catch (Exception e2) {
					exception = e2;
				}
			}
		} catch (IOException e) {
			log.warn(" failed to parse summary page of " + getSiteName(), e);
			throw e;
		}
	}

	private Job getJobDetails(Element el) {
		String url = el.select("a").first().attr("href");
		Job job = new Job();
		job.setUrl(url);
		try {
			Document doc = Jsoup.connect(url).get();
			Element jobSec = doc.select("div.storyContentWrapper").first();			
			Element jobE = doc.selectFirst("div[class=story_title]>h1");
			job.setTitle(jobE.text());
			job.setName(job.getTitle());
			job.setLocation(jobSec.select("p").first().text());		
			job.setSpec(jobSec.select("div.storytext").text());
			Element date=doc.select("p[class=byline meta]").first();
			job.setPostedDate(parseDate(date.text().split("T")[0].trim(), DF));
			return job;
		} catch (IOException iox) {
			log.warn("Failed parse job details of" + job.getUrl(), iox);
		}
		return null;
	}

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return null;
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
