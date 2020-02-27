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
import io.naztech.jobharvestar.scraper.AbstractScraper;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * BOC HONG KONG HOLDING job parser. <br>
 * URL: https://hk.jobsdb.com/hk/jobs/companies/bank-of-china-hong-kong-limited/
 * 
 * @author BM Al-Amin
 * @author tanmoy.tushar
 * @author fahim.reza
 * @since 2019-03-05
 */
@Service
@Slf4j
public class BocHongkongHolding extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.BOC_HONG_KONG_HOLDINGS;
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd MMM yyyy");

	private int expectedJobCount;
	private Exception exception;
	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	public void scrapJobs() {
		startSiteScrapping(getSiteMetaData(getSiteName()));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		int totalPage = getTotalPage(site);
		for (int i = 1; i <= totalPage; i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			String url = site.getUrl() + i;
			try {
				browseJobList(url, site);
			} catch (Exception e) {
				log.error("Failed to parse job list of " + url, e);
			}
		}
	}

	private void browseJobList(String url, SiteMetaData site) throws IOException, PageScrapingInterruptedException {
		Document doc = Jsoup.connect(url).get();
		Elements rowList = doc.select("div[data-automation=job-title]>span>a");
		for (Element element : rowList) {
			String jobUrl = element.attr("href");
			if (isStopped()) throw new PageScrapingInterruptedException();
			try {
				saveJob(getJobDetail(jobUrl), site);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job details of " + jobUrl, e);
			}
		}
	}

	private Job getJobDetail(String url) throws IOException {
		Job job = new Job(url);
		Document doc = Jsoup.connect(job.getUrl()).get();
		Element jobE = doc.selectFirst("span[class=JG37Vx2 _2qbhd1z _1KRg4Ns sATlVcU]>span");
		job.setTitle(jobE.text());
		job.setName(job.getTitle());
		Elements jobLocPost = doc.select("span[class=JG37Vx2 _1fSwe2M]>span");
		if (jobLocPost.size() > 1) {
			String loc = jobLocPost.get(0).text().trim();
			if(!loc.equalsIgnoreCase("Not Specified")) job.setLocation(jobLocPost.get(0).text().trim());
			job.setPostedDate(parseDate(jobLocPost.get(1).text().split("on")[1].trim(), DF));
		}
		jobE = doc.selectFirst("a[data-automation=applyNowButton]");
		job.setApplicationUrl(jobE.attr("href"));
		jobE = doc.selectFirst("div[data-automation=jobDescription]");
		job.setSpec(jobE.text());
		if (jobE != null) {
			String[] parts = job.getSpec().split(":");
			if (parts.length > 7) {
				if (parts[1].contains("Employment Type")) job.setReferenceId(parts[1].replace("Employment Type", "").trim());
				if (parts[2].contains("Departments")) job.setType(parts[2].replace("Departments", "").trim());
				if (parts[3].contains("Job Functions")) job.setCategory(parts[3].replace("Job Functions", "").trim());
			}
		}
		return job;
	}

	private int getTotalPage(SiteMetaData site) throws IOException {
		Document doc = Jsoup.connect(site.getUrl()).get();
		Element el = doc.selectFirst("span[class=FYwKg _28iNm C6ZIU_1Fy _1PM5y_1Fy ELZOd_1Fy _29m7__1Fy _6ufcS_1Fy _2WTa0_1Fy]");
		String totalJob = el.text().split("of")[1].trim().split(" ")[0].trim();
		expectedJobCount = Integer.parseInt(totalJob);
		return getPageCount(totalJob, 30);
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
