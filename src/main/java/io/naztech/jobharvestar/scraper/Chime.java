package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.FrameWindow;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * Chime job site scraper. <br>
 * URL: https://www.chimebank.com/job-openings/
 * 
 * @author muhammad.tarek
 * @since 2019-04-01
 */
@Slf4j
@Service
public class Chime extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.CHIME;
	private static WebClient webClient = null;
	private int expectedJobCount = 0;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception {
		startSiteScrapping(getSiteMetaData(getSiteName()));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		webClient = getFirefoxClient();
		HtmlPage page = webClient.getPage(siteMeta.getUrl());
		webClient.waitForBackgroundJavaScript(TIME_10S*3);
		browseJobList(getInnerPage(page.getFrames()), siteMeta);
	}

	private HtmlPage getInnerPage(List<FrameWindow> frames) {
		for (FrameWindow frame : frames) {
			if ("grnhse_iframe".equals(frame.getFrameElement().getId())) return (HtmlPage) frame.getEnclosedPage();
		}
		return null;
	}

	private void browseJobList(HtmlPage innerPage, SiteMetaData siteMeta) {
		try {
			List<HtmlElement> categoryElements = innerPage.getByXPath("//section[@class='level-0']");
			for (HtmlElement categoryE : categoryElements) {
				String category = categoryE.getElementsByTagName("h3").get(0).getTextContent().trim();
				List<HtmlElement> jobListE = categoryE.getElementsByAttribute("div", "class", "opening");
				expectedJobCount += jobListE.size();
				for (HtmlElement jobE : jobListE) {
					if (isStopped()) throw new PageScrapingInterruptedException();
					HtmlElement linkE = jobE.getElementsByTagName("a").get(0);
					Job job = new Job(linkE.getAttribute("href"));
					job.setTitle(linkE.getTextContent());
					job.setName(job.getTitle());
					job.setCategory(category);
					job.setLocation(jobE.getElementsByAttribute("span", "class", "location").get(0).getTextContent());
					try {
						saveJob(getJobDetail(job), siteMeta);
					} catch (Exception e) {
						exception = e;
					}
				}
			}
		} catch (PageScrapingInterruptedException e) {
			log.warn(getSiteName() + "Failed to parse job details", e);
		}
	}

	protected Job getJobDetail(Job job) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		HtmlPage page = webClient.getPage(job.getUrl());
		webClient.waitForBackgroundJavaScript(TIME_4S);
		List<HtmlElement> row = getInnerPage(page.getFrames()).getByXPath("//div[@id='content']/*");
		job.setSpec(row.get(0).getTextContent());
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
		webClient.close();
	}

	@Override
	protected Exception getFailedException() {
		return exception;
	}
}
