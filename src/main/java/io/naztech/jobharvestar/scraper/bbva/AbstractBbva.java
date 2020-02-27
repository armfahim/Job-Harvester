package io.naztech.jobharvestar.scraper.bbva;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.scraper.AbstractScraper;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
/**
 * All Jobsites of https://careers.bbva.com<br>
 * 
 * BBVA ASIA COMPASS URL: https://careers.bbva.com/compass/category-jobs-results/
 * BBVA EUROPE URL: https://careers.bbva.com/europa/jobs-results/
 * BBVA PROVINCIAL URL: https://careers.bbva.com/provincial/jobs-results/
 * BBVA BANCOMER URL: https://careers.bbva.com/bancomer/category-jobs-results/
 * BBVA CONTINENTAL URL: https://careers.bbva.com/continental/jobs-results/
 * BBVA FRANCES URL: https://careers.bbva.com/frances/jobs-results/
 * BBVA URUGUAY URL: https://careers.bbva.com/uruguay/jobs-results/
 * BBVA COLOMBIA URL: https://careers.bbva.com/colombia/jobs-results/
 * BBVA ESPANA URL: https://careers.bbva.com/espana/jobs-results/
 * BBVA PARAGUAY URL: https://careers.bbva.com/paraguay/jobs-results/
 * 
 * @author naym.hossain
 * @since 2019-03-04
 */
@Service
public abstract class AbstractBbva extends AbstractScraper implements Scrapper {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private static final String PAGECOUNT_PATH = "//*[@id=\"bloque_contenidoPrincipal\"]/div/section/div/aside[1]/p";
	private static final String JOBLIST_PATH = "//*[@id=\"bloque_contenidoPrincipal\"]/div/section/div/ul/li/article/h1/a";
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("d['st']['nd']['rd']['th'] MMMM yyyy");
	private static final int JOB_PER_PAGE = 10;
	private WebClient client = null;
	private int expectedJobCount;
	private static Document document;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		client = getFirefoxClient();
		SiteMetaData site = getSiteMetaData(getSiteName());
		if (log.isTraceEnabled()) log.trace(site.getUrl());	
		setBaseUrl(site);
		startSiteScrapping(site);
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws InterruptedException, IOException{
		try {
			List<String> jobUrl = new ArrayList<>();
			HtmlPage page = client.getPage(siteMeta.getUrl());
			client.waitForBackgroundJavaScript(5000);
			HtmlElement el = page.getBody().getFirstByXPath(PAGECOUNT_PATH);
			int totalPage = getPageCount(el.getTextContent().split("of")[1].trim(), JOB_PER_PAGE);
			int activePage = 1;
			while (true) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				jobUrl.addAll(getSummaryPages(page));
				
				if (activePage == totalPage) break;
				
				HtmlElement nextPage = page.getBody().getOneHtmlElementByAttribute("a", "value",
						Integer.toString(++activePage));
				
				page = nextPage.click();
				client.waitForBackgroundJavaScript(10 * 1000);
			}
			expectedJobCount = jobUrl.size();
			for (String url : jobUrl) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				try {
				saveJob(getJobDetails(url), siteMeta);
				}
				catch(Exception e) {
					exception = e;
				}
				Thread.sleep(3000);
			}
		} catch (IOException e) {
			log.warn(getSiteName() + "Failed to load Page due to ", e);
			throw e;
		} finally {
			client.close();
		}
	}

	protected List<String> getSummaryPages(HtmlPage summaryPage) throws PageScrapingInterruptedException {
		try {
			List<String> jobSummaryUrl = new ArrayList<>();
			List<HtmlElement> jobList = summaryPage.getBody().getByXPath(JOBLIST_PATH);
			for (HtmlElement list : jobList) {
				if (isStopped()) throw new PageScrapingInterruptedException();				
				jobSummaryUrl.add(list.getAttribute("href"));
			}
			return jobSummaryUrl;
		} catch (NullPointerException | FailingHttpStatusCodeException e) {
			log.warn(getSiteName() + "-> Job Detail Page failed due to ", e);
			return null;
		}
	}

	protected Job getJobDetails(String url) {
		try {
			document = Jsoup.connect(url).get();
			Job job = new Job(url);
			Element jobTitle = document.selectFirst("h1[class=pagina_titulo]");
			job.setTitle(jobTitle.text().split("-")[0]);
			job.setName(job.getTitle());
			Elements jobE = document.select("ul[class=lista_datosOferta]>li");
			if(jobE!=null) {
			for(int i=0;i<jobE.size();i++)
				{
				   if(jobE.get(i).text().contains("Location")){
					   job.setLocation(jobE.get(i).text().split(":")[1].trim());
					}
				   else if(jobE.get(i).text().contains("Published on")){
					   job.setPostedDate(LocalDate.parse(jobE.get(i).text().split(":")[1].trim(),DF));
				   }
				}
			}
			 Element appUrl = document.selectFirst("a[id=btninscribete]");
			 if(appUrl!=null)
				 job.setApplicationUrl(appUrl.attr("href"));
			 Element jobSpec=document.selectFirst("div[class=moduloContenido_ofertaDetalle_descripcion]");
			 if(jobSpec != null)
				 job.setSpec(jobSpec.text());
			return job;
		} catch (IOException | FailingHttpStatusCodeException e) {
			log.warn("Failed parse job details of "+ url, e);
			return null;
		}
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
	public abstract String getSiteName();

	protected abstract void setBaseUrl(SiteMetaData site);

	@Override
	protected Exception getFailedException() {
		return exception;
	}
}

