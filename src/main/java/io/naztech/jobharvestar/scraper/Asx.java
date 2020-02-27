package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;

import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;
/**
 * ASX Jobsite Scraper<br>
 * URL: https://www.asx.com.au/about/job-opportunities.htm
 * Note: Job detail Page is in PDF format
 * 
 * @author Mahmud Rana
 * @since 2019-03-04
 */
@Service
@Slf4j
public class Asx extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.ASX;
	private static final String ROW_EL_PATH = "//*[@id=\"content\"]/div/table/tbody/tr";
	private static final String TITLE_EL_PATH = ROW_EL_PATH+"/td[1]/a";
	private static final String TYP_EL_PATH = ROW_EL_PATH+"/td[2]";
	private static final String CAT_EL_PATH = ROW_EL_PATH+"/td[3]";
	private static final String LOC_EL_PATH = ROW_EL_PATH+"/td[4]";
	private int expectedJobCount;
	private Exception exception;
	
	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(getSiteName()));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		try (WebClient client = getFirefoxClient()) {
			HtmlPage page = client.getPage(siteMeta.getUrl());
			client.waitForBackgroundJavaScript(TIME_5S);
			List<HtmlElement> rowList = page.getBody().getByXPath(ROW_EL_PATH);
			expectedJobCount =  rowList.size();
			for (int i=1 ; i<rowList.size()-1; i++) {
				Job job = new Job(((HtmlElement)page.getBody().getByXPath(TITLE_EL_PATH).get(i)).getAttribute("href"));
				job.setTitle(((HtmlElement)page.getBody().getByXPath(TITLE_EL_PATH).get(i)).getTextContent());
				job.setName(job.getTitle());
				job.setCategory(((HtmlElement)page.getBody().getByXPath(CAT_EL_PATH).get(i)).getTextContent());
				job.setType(((HtmlElement)page.getBody().getByXPath(TYP_EL_PATH).get(i)).getTextContent());
				job.setLocation(((HtmlElement)page.getBody().getByXPath(LOC_EL_PATH).get(i)).getTextContent());
				try {
					saveJob(getJobDetail(job), siteMeta);					
				} catch (Exception e) {
					exception = e;
				}
			}
		} catch (FailingHttpStatusCodeException e) {
			log.warn(getSiteName()+" fails to load summary page.", e);
			throw e;
		}
	}

	private Job getJobDetail(Job job) {
		StringBuffer sb = getParsedPdf(job.getUrl());
		if (sb!=null) job.setSpec(sb.toString());
		return job;
	}
	
	private StringBuffer getParsedPdf(String urlPath) {
		PdfReader reader = null;
		try {
			URL url = new URL(urlPath);
			reader = new PdfReader(url);
			StringBuffer sb = new StringBuffer();
			PdfReaderContentParser parser = new PdfReaderContentParser(reader);
			TextExtractionStrategy strategy;
			for (int i = 1; i <= reader.getNumberOfPages(); i++) {
				strategy = parser.processContent(i, new SimpleTextExtractionStrategy());
				sb.append(strategy.getResultantText());
			}
			return sb;
		} catch (MalformedURLException e) {
			log.warn(getSiteName() + "Fails to load supplied url" + urlPath, e);
		} catch (IOException e) {
			log.warn(getSiteName() + " Fails to find pdf " + urlPath);
		} finally {
			if (reader != null)
				reader.close();
		}		
		return null;
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
