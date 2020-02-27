package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

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
 * Nomura Japan job site parser<br>
 * URL: https://progres02.jposting.net/pgnomura/u/job.phtml
 * 
 * @author Armaan Seraj Choudhury
 * @author tanmoy.tushar
 * @since 2019-01-20
 */
@Slf4j
@Service
public class NomuraJapan extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.NOMURA_HOLDINGS_JAPAN;
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception {
		startSiteScrapping(getSiteMetaData(ShortName.NOMURA_HOLDINGS_JAPAN));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		this.baseUrl = site.getUrl().substring(0, 42);
		Document doc = Jsoup.connect(site.getUrl()).get();
		Elements rowList = doc.select("table>tbody>tr>td>a");
		List<Element> jobList = rowList.stream().filter(el -> el.attr("href").contains("job.phtml")).collect(Collectors.toList());
		expectedJobCount = jobList.size();
		browseJobList(jobList, site);
	}

	private void browseJobList(List<Element> jobList, SiteMetaData site) throws InterruptedException {
		for (Element el : jobList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Job job = new Job(getBaseUrl() + el.attr("href"));
			job.setTitle(el.text().trim());
			job.setName(job.getTitle());
			try {
				saveJob(getJobDetail(job), site);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of " + job.getUrl(), e);
			}
		}
	}

	private Job getJobDetail(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).get();
		Elements jobEls = doc.select("table>tbody>tr>td>table>tbody>tr>td>table>tbody>tr");
		if (jobEls.size() == 0)	throw new NullPointerException();
		for (Element el : jobEls) {
			if (el.text().contains("Description")) job.setSpec(el.text().trim());
			if (el.text().contains("Requirements"))	job.setPrerequisite(el.text().trim());
			if (el.text().contains("Location"))	job.setLocation(el.text().split("Location")[1].trim());
		}
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