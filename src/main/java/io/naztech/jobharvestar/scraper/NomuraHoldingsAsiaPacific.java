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
 * Nomura Holdings Asia Pacific<br>
 * URL:
 * https://nomuraexperiencedtalentacquisition.tal.net/vx/lang-en-GB/mobile-0/appcentre-ext/brand-4/xf-13a61fff518a/candidate/jobboard/vacancy/3/adv/
 * 
 * @author Benajir Ullah
 * @author iftekar.alam
 * @since 2019-01-30
 */
@Slf4j
@Service
public class NomuraHoldingsAsiaPacific extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.NOMURA_HOLDINGS_ASIA_PACIFIC;
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception {
		startSiteScrapping(getSiteMetaData(ShortName.NOMURA_HOLDINGS_ASIA_PACIFIC));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		Document doc = Jsoup.connect(siteMeta.getUrl()).get();
		Elements jobList = doc.select("table[class=table solr_search_list]>tbody>tr");
		expectedJobCount = jobList.size();
		for (Element row : jobList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Job job = new Job(row.select("td>a").attr("href"));
			job.setTitle(row.select("td>a").text().trim());
			job.setName(job.getTitle());
			job.setReferenceId(row.getElementsByTag("td").get(0).text().trim());
			job.setCategory(row.getElementsByTag("td").get(4).text().trim());
			job.setLocation(row.getElementsByTag("td").get(3).text().trim());
			try {
				saveJob(getJobDetail(job), siteMeta);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse details of "+job.getUrl(),e);
			}
		}
	}

	private Job getJobDetail(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).get();
		Element type = doc.getElementById("datafield_85525__1");
		job.setType(type.text().trim());
		Element spce = doc.getElementById("datafield_81251__1");
		job.setSpec(spce.text().trim());
		Element preReq = doc.getElementById("datafield_85581__1");
		job.setPrerequisite(preReq.text().trim());
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
