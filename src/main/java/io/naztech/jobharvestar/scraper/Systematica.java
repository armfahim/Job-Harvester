package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;

/**
 * Systematica<br> 
 * URL: https://www.systematica.com/careers/#jobs
 * 
 * @author tohedul.islum
 * @since 2019-03-10
 */
@Service
public class Systematica extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.SYSTEMATICA_INVESTMENTS;
	private String baseUrl;
	private static WebClient client = null;
	private int expectedJobCount;
	private Exception exception;
	
	@Override
	public void scrapJobs() throws Exception{
		client = getChromeClient();
		startSiteScrapping(getSiteMetaData(ShortName.SYSTEMATICA_INVESTMENTS));

	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 27);
		HtmlPage page = client.getPage(siteMeta.getUrl());
		List<HtmlElement> jobList = page.getByXPath("//article[@class='job']");
		expectedJobCount = jobList.size();
		for (HtmlElement li : jobList) {
			try {
				Job job = new Job();
				HtmlElement name = li.getElementsByTagName("h4").get(0);
				job.setName(name.asText());
				job.setTitle(name.asText());
				job.setUrl(getBaseUrl() + getJobHash(job));
				job.setLocation(li.getElementsByTagName("p").get(0).asText());
				HtmlElement spec = (HtmlElement) li.getByXPath("//div[@class='description']").get(0);
				job.setSpec(spec.asText());
				HtmlElement appUrl = li.getElementsByTagName("a").get(1);
				job.setApplicationUrl(appUrl.getAttribute("href"));
				saveJob(job, siteMeta);				
			} catch (Exception e) {
				exception = e;
			}
		}
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
		client.close();
	}

	@Override
	protected Exception getFailedException() {
		return exception;
	}
}
