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
 * Ntt Data jobs site parser <br>
 * URL: https://careers-inc.nttdata.com/search/
 * 
 * @author fahim.reza
 * @since 2019-10-21
 */
@Slf4j
@Service
public class NttData extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.NTT_DATA;
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("MMM dd, yyyy");
	private String baseUrl;
	private static final int JOB_PER_PAGE = 25;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception {
		startSiteScrapping(getSiteMetaData(getSiteName()));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 31);
		Document doc = Jsoup.connect(siteMeta.getUrl()).get();
		String totalJob = getTotalJob(doc);
		if (totalJob == null) {
			throw new NullPointerException("Total page number not found");
		}
		expectedJobCount = Integer.parseInt(totalJob);
		log.info("Total Job Found: " + getExpectedJob());
		int totalPage = getPageCount(totalJob, JOB_PER_PAGE);
		for (int i = 0, j = 0; i <= totalPage; i++, j += 25) {
			if (isStopped())
				throw new PageScrapingInterruptedException();
			getSummaryPage(baseUrl + "/search/?q=&sortColumn=referencedate&sortDirection=desc&startrow=" + j, siteMeta);
		}

	}

	private void getSummaryPage(String url, SiteMetaData siteMeta) throws IOException {
		Document doc = Jsoup.connect(url).get();
		Job job = new Job();
		try {
			Elements jobList = doc.select("div[class=jobdetail-phone visible-phone]");
			for (Element el : jobList) {
				job.setUrl(baseUrl
						+ el.getElementsByClass("jobTitle visible-phone").get(0).getElementsByTag("a").attr("href"));
				job.setTitle(el.getElementsByClass("jobTitle visible-phone").get(0).getElementsByTag("a").text());
				job.setName(job.getTitle());
				job.setLocation(el.getElementsByClass("jobLocation visible-phone").get(0)
						.getElementsByClass("jobLocation").text());
				job.setPostedDate(parseDate(el.getElementsByClass("jobDate visible-phone").text(), DF));
				try {
					saveJob(getJobDetails(job), siteMeta);
				} catch (Exception e) {
					exception = e;
					log.warn("Failed to parse job details of : " + job.getUrl());
				}
			}

		} catch (Exception e) {
			exception = e;
			log.warn("Failed to parse summary page of : " + url);
		}
	}

	private Job getJobDetails(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).get();
		Element elRefer = doc.selectFirst("span[class=jobdescription] > p > strong");
		Element category = doc.selectFirst("span[itemprop=industry]");
		Elements spec = doc.select("div[class=job]");
		String refer = elRefer.text().split(":")[1].trim();
		System.out.println("elRefer: " + refer);
		if (refer.contains("Helpdesk Supervisor")) {
			Element elRefer2 = doc.select("span[class=jobdescription] > p").get(1);
			String refer2 = elRefer2.text().split(":")[1].trim();
			if (refer2.contains("[[")) {
				job.setReferenceId(refer2.substring(2).split("]")[0].trim());
				System.out.println("If Contains and HelpDest and [[: " + refer2.substring(2).split("]")[0].trim());
			} else {
				job.setReferenceId(refer2);
				System.out.println("elRefer2: " + refer2);
			}
		} else {
			if (refer.contains("[[")) {
				System.out.println("If only contails [[" + refer.substring(2).split("]]")[0].trim());
				job.setReferenceId(refer.substring(2).split("]]")[0].trim());
			} else {
				System.out.println("Nothing Contains: " + refer);
				job.setReferenceId(refer);
			}
		}
		job.setCategory(category.text());
		job.setSpec(spec.text().split("ID:")[1].trim());
		return job;
	}

	private String getTotalJob(Document doc) {
		Element totalJob = doc.selectFirst("span[class=paginationLabel]");
		return totalJob.text().split("of")[1].trim();
	}

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return baseUrl;
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
