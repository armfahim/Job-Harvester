package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * SWED Bank.<br>
 * URL: https://swedbank.easycruit.com/intranet/external/index.html#page:1
 * 
 * @author farzana.islam
 * @since 2019-01-21
 */
@Slf4j
@Service
public class SwedBank extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.SWEDBANK;
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;
	private DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	private DateTimeFormatter DF1 = DateTimeFormatter.ofPattern("dd.MM.yyyy");
	private DateTimeFormatter DF2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	@Override
	public void scrapJobs() throws Exception {
		startSiteScrapping(getSiteMetaData(ShortName.SWEDBANK));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		Document doc = Jsoup.connect(siteMeta.getUrl() + "#page:1").get();
		this.baseUrl = siteMeta.getUrl().substring(0, 30);	
		Elements rowList = doc.select("ul[id=vacancyList]>li>div>h2>a");
		expectedJobCount=rowList.size();
		for (Element el : rowList) {
			Job job = new Job(getBaseUrl()+el.attr("href"));
			try {
				saveJob(getJobDetails(job), siteMeta);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job details of " + job.getUrl(), e);
			}
		}
	}

	private Job getJobDetails(Job job) throws InterruptedException, IOException {
		Document doc = Jsoup.connect(job.getUrl()).get();
		Element jobE = doc.selectFirst("h1");
		job.setTitle(jobE.text().trim());
		job.setName(job.getTitle());
		jobE = doc.selectFirst("div[class=jd-description]");
		job.setSpec(jobE.text().trim());
		jobE = doc.selectFirst("div[class=jd-department]>section>p");
		job.setCategory(jobE.text().trim());
		jobE = doc.selectFirst("div[class=jd-location]>section>p");
		String location=jobE.text().trim();
		if (location.contains("-")) job.setLocation(location.split("-")[0].trim());
		else job.setLocation(location);
		
		jobE=doc.selectFirst("div[class=jd-deadline]>section>p");
		job.setDeadline(parseDate(jobE.text().trim(), DF,DF1,DF2));
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
