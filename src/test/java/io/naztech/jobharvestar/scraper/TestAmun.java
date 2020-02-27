package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class TestAmun {

	private String pageUrl = "https://career5.successfactors.eu/career?company=Pioneer&career_ns=job_listing_summary&navBarLevel=JOB_SEARCH";
	private String baseUrl;
	private static WebClient webClient = null;
	@Test
	public void getScrapedJobs() {
		try {
			System.out.println("page Url: "+ pageUrl);
			this.baseUrl = pageUrl.substring(0, 33);
			System.out.println("base Url: "+baseUrl);
			HtmlPage page = getWebClient().getPage(pageUrl);
			Thread.sleep(5000);
			System.out.println(page.getTitleText());
		//	List<Job> list = new ArrayList<>();
////			while (page != null) {
			List<HtmlAnchor> anchorList = page.getByXPath("//a[@class='jobTitle']");
			System.out.println(anchorList.size());
//			for (HtmlAnchor htmlAnchor : anchorList) {
////					if (isStopped()) throw new PageScrapingInterruptedException();
//				Job job = new Job(baseUrl + htmlAnchor.getHrefAttribute());
//				System.out.println("Job Url: " + job.getUrl());
//				job.setTitle(htmlAnchor.asText());
//				System.out.println("Job Title" + job.getTitle());
//				job.setName(job.getTitle());
//				list.add(job);
//			}
//				page = getNextPage(page);
//			}
//			for (Job job : list) {
//				saveJob(getJobDetails(job), siteMeta);
//			}

		} catch (FailingHttpStatusCodeException | IOException e) {
			//log.warn(SITE + " Failed to get page  :", e);
		} catch (InterruptedException e) {
			//log.warn(SITE + " Page loading interrupted :", e);
		}
	}
	
	private WebClient getWebClient() {
		if (webClient == null) {
			webClient = new WebClient(BrowserVersion.FIREFOX_52);
			webClient.getOptions().setDoNotTrackEnabled(true);
			webClient.getOptions().setMaxInMemory(0);
			webClient.getOptions().setTimeout(30 * 1000);
			webClient.getOptions().setCssEnabled(false);
		}
		return webClient;
	}

}
