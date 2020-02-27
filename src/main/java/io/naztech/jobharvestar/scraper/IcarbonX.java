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
 * iCarbonX jobs site parser <br>
 * URL: https://www.icarbonx.com/en/careers.html
 * 
 * @author armaan.choudhury
 * @since 2019-03-12
 */
@Service
@Slf4j
public class IcarbonX extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.ICARBONX;
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(ShortName.ICARBONX));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		getSummaryPage(siteMeta);
	}

	private void getSummaryPage(SiteMetaData siteMeta) throws InterruptedException, IOException {
		try {
			Elements list = Jsoup.connect(siteMeta.getUrl()).get().select("div.Careers > div > ul > li");
			expectedJobCount = list.size();
			for (Element el : list) {
				if (isStopped())
					throw new PageScrapingInterruptedException();
				String joburl = "https:" + el.child(1).child(0).child(0).attr("href");
				Job job = new Job();
				job.setUrl(joburl);
				job.setTitle(el.child(0).text().trim());
				try {
					saveJob(getJobDetail(job), siteMeta);
				} catch (Exception e) {
					exception = e;
				}
			}
		} catch (IOException e) {
			log.warn(" failed to parse summary page of " + getSiteName(), e);
			throw e;
		}
	}

	private Job getJobDetail(Job job) throws IOException {
		String jobDescription = null;
		Document doc = Jsoup.connect(job.getUrl()).get();
		Elements descE = doc.select("div.new_content > div");
		jobDescription = descE.get(1).wholeText();
		job.setName(job.getTitle());
		job.setSpec(jobDescription);
		return job;
	}

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return this.baseUrl;
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
