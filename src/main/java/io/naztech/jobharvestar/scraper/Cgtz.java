package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;

/**
 * CGTZ job site scrapper. <br>
 * URL: https://www.cgtz.com/about/takeJob.html
 * 
 * @author Shadman Shahriar
 * @since 2019-03-24
 */
@Service
public class Cgtz extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.CGTZ;
	private static WebClient webClient = null;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(getSiteName()));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		webClient = getChromeClient();
		HtmlPage page = webClient.getPage(siteMeta.getUrl());
		List<HtmlElement> jobList = page.getByXPath("//li[@class='questionText  closeArrow ']");
		expectedJobCount = jobList.size() + 1;
		List<HtmlElement> jobList1 = page.getByXPath("//ul[@class='foldText']/li");
		try {
			for (int i = 0; i < jobList1.size(); i += 2) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				Job job = new Job();
				if (i != 0) {
					jobList1.get(i).click();
					job.setTitle(jobList1.get(i).asText().trim());
					job.setName(job.getTitle());
					job.setSpec(jobList1.get(i + 1).asText().trim());
				} else {
					job.setTitle(jobList1.get(i).asText().trim());
					job.setName(job.getTitle());
					job.setSpec(jobList1.get(i + 1).asText().trim());
				}
				job.setUrl(getJobHash(job));
				saveJob(job, siteMeta);
			}

		} catch (Exception e) {
			exception = e;
		}

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
