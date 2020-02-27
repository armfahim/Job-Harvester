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
 * NerdWallet job site parsing class. <br>
 * URL: https://www.nerdwallet.com/careers?trk=nw_gf_5.0
 * 
 * @author marjana.akter
 * @since 2019-04-03
 */
@Slf4j
@Service
public class NerdWallet extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.NERDWALLET;
	private static String baseUrl;
	private int expectedJobCount = 0;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		baseUrl = site.getUrl().substring(0, 26);
		getSummaryPages(site);
	}

	private void getSummaryPages(SiteMetaData site) throws PageScrapingInterruptedException, IOException {
		Document doc;
		try {
			doc = Jsoup.connect(site.getUrl()).get();
			Elements jobUrl = doc.select("a[class=departments__tile]");
			for (Element element : jobUrl) {
				if (isStopped())
					throw new PageScrapingInterruptedException();
				try {
					getJobDetails(getBaseUrl() + element.attr("href"), site);
				} catch (Exception e) {
					exception = e;
				}
			}
		} catch (IOException e) {
			log.warn(" failed to parse summary page of " + getSiteName(), e);
			throw e;
		}
	}

	private void getJobDetails(String url, SiteMetaData site) throws PageScrapingInterruptedException {
		Document doc;
		try {
			doc = Jsoup.connect(url).get();
			Elements jobList = doc.select("div[class=joblist__tile--cont]");
			expectedJobCount += jobList.size();
			for (Element el : jobList) {
				if (isStopped())
					throw new PageScrapingInterruptedException();
				Job job = new Job();
				job.setTitle(el.select("p[class=joblist__header _1oz19 _2d7ND]").text());
				job.setName(job.getTitle());
				job.setUrl(getBaseUrl() + el.select("div[class=joblist__tile--cont]>a").attr("href"));
				String[] part = el.select("p[class=joblist__jobs _2t0Jv]").text().split("or");
				if (part.length == 2) {
					job.setLocation(part[0].trim());
					job.setComment(part[1].trim());
				} else {
					job.setLocation(el.selectFirst("p[class=joblist__jobs _2t0Jv]").text());
				}
				getJobSpec(getBaseUrl() + el.select("div[class=joblist__tile--cont]>a").attr("href"), job);
				saveJob(job, site);
			}

		} catch (IOException e) {
			log.warn("Failed to parse job details " + e);
		}
	}

	public Job getJobSpec(String url, Job job) {
		try {
			Document doc = Jsoup.connect(url).get();
			job.setSpec(doc.select("div[class=job__cont _2m6fE]").text());
			job.setApplicationUrl(job.getUrl()
					+ doc.select("div[class=job__hdr--apply-mobile _23e1j _1SRCr KH8lp nVuqt]>a").attr("href"));
			return job;
		} catch (Exception e) {
			log.warn(" failed to parse detail page of" + job.getUrl(), e);
		}
		return null;
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
