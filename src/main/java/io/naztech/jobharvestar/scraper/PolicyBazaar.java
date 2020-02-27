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
import lombok.extern.slf4j.Slf4j;

/**
 * Policy Bazaar job site scraper. <br>
 * URL: https://www.policybazaar.com/careers/
 * 
 * @author a.s.m. tarek
 * @since 2019-03-13
 */
@Slf4j
@Service
public class PolicyBazaar extends AbstractScraper implements Scrapper {

	private static final String SITE = ShortName.POLICYBAZAAR;
	private String baseUrl;
	private static WebClient client = null;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		client = getFirefoxClient();
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		getSummaryPage(site.getUrl(), site);
	}

	private void getSummaryPage(String url, SiteMetaData site) throws InterruptedException, IOException {
		try {
			HtmlPage page = client.getPage(url);
			List<HtmlElement> el = page.getByXPath("//div[@class='openingsBlock']");
			expectedJobCount = el.size();
			for (HtmlElement tr : el) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				try {
					Job job = new Job();
					List<HtmlElement> title = tr.getElementsByTagName("label");
					job.setTitle(title.get(0).asText());
					job.setName(title.get(0).asText());
					List<HtmlElement> des = tr.getElementsByTagName("p");
					job.setComment(des.get(0).asText());
					List<HtmlElement> req = tr.getElementsByTagName("span");
					job.setPrerequisite(req.get(0).asText());
					List<HtmlElement> spec = tr.getByXPath("//div[@class='posInfo fLeft fullWidth']");
					job.setSpec(spec.get(0).getTextContent());
					job.setUrl(getJobHash(job));
					saveJob(job, site);					
				} catch (Exception e) {
					exception = e;
				}
			}
		} catch (IOException e) {
			log.warn(SITE + " Failed to parse Summary page of " + url, e);
			throw e;
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
