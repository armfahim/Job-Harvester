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
 * Yixia job site scrapper.<br> 
 * URL: http://www.yixia.com/hiring Data is Chinese
 * 
 * @author Alif Choyon
 * @since 2019-03-18
 */
@Slf4j
@Service
public class Yixia extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.YIXIA;
	private int expectedJobCount;
	private Exception exception;
	private static final String USER_AGENT = "Mozilla/5.0 (iPad; U; CPU OS 3_2_1 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Mobile/7B405";
	
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
			Document doc = Jsoup.connect(site.getUrl()).userAgent(USER_AGENT).timeout(TIME_1M).get();
			Elements titleListE = doc.select("div[class^=job_container]");
			expectedJobCount = titleListE.size();
			for (Element element : titleListE) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				try {
					saveJob(getJobDetails(element), site);					
				} catch (Exception e) {
					exception = e;
				}
			}
		} catch (IOException e) {
			log.warn("Failed to parse job details" + site.getUrl(), e);
			throw e;
		}
	}

	public Job getJobDetails(Element el) {
		Job job = new Job();
		job.setTitle(el.getElementsByClass("job_name").get(0).text());
		job.setName(job.getTitle());
		job.setType(el.getElementsByClass("job_type").get(0).text());
		job.setLocation(el.getElementsByClass("job_addr").get(0).text());
		job.setUrl(getJobHash(job));
		job.setSpec(el.getElementsByClass("job_content").get(0).text());
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