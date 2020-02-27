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
 * Principal Financial Grp Mx Job Site Scraper. <br>
 * URL: https://www.bumeran.com.mx/empleos-busqueda-grupo-financiero-principal.html
 * 
 * @author Sanzida Hoque
 * @author tanmoy.tushar
 * @since 2019-24-02
 */
@Slf4j
@Service
public class PrincipalFinancialGrpMx extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.PRINCIPAL_FINANCIAL_GRP_MX;
	private static final int JOBS_PER_PAGE = 30;
	private String baseUrl;
	private int expectedJobCount = 0;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(ShortName.PRINCIPAL_FINANCIAL_GRP_MX));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 26);
		int totalPages = getTotalPage(siteMeta.getUrl());
		for (int pageNum = 1; pageNum <= totalPages; pageNum++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			String url = getBaseUrl() + "/empleos-busqueda-grupo-financiero-principal-pagina-" + pageNum + ".html";
			try {
				browseJobList(url, siteMeta);
			} catch (Exception e) {
				log.warn("Failed to pase job list page of " + url, e);
			}
		}
	}

	private void browseJobList(String url, SiteMetaData site) throws IOException, PageScrapingInterruptedException {
		Document doc = Jsoup.connect(url).get();
		Elements rowList = doc.select("div[class=col-sm-9 col-md-10 col-xs-9 wrapper]>a");
		for (Element el : rowList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			String jobUrl = getBaseUrl() + el.attr("href");
			try {
				saveJob(getJobDetail(jobUrl), site);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of " + url, e);
			}
		}
	}

	private Job getJobDetail(String url) throws IOException {
		Job job = new Job(url);
		Document doc = Jsoup.connect(job.getUrl()).get();
		Element jobE = doc.selectFirst("h1");
		job.setTitle(jobE.text().trim());
		job.setName(job.getTitle());
		jobE = doc.selectFirst("div[class=aviso_description]");
		job.setSpec(jobE.text().trim());
		Elements jobInfo = doc.select("div[class=col-sm-12 col-md-3 col-lg-2 spec_attr]");
		for (Element element : jobInfo) {
			if (element.text().contains("Lugar de Trabajo")) job.setLocation(element.nextElementSibling().text());
			if (element.text().contains("Tipo de puesto")) job.setType(element.nextElementSibling().text());
			if (element.text().contains("Área")) job.setCategory(element.nextElementSibling().text());
			if (element.text().contains("Publicado")) {
				String date = element.nextElementSibling().text().split(" ")[2].trim() + " days ago";
				job.setPostedDate(parseAgoDates(date));
			}
		}
		return job;
	}

	private int getTotalPage(String url) throws IOException {
		try {
			Document doc = Jsoup.connect(url).get();
			Element el = doc.selectFirst("div[class=listado-empleos col-sm-9 col-md-9]>h1>strong");
			String totalJobs = el.text().trim();
			expectedJobCount = Integer.parseInt(totalJobs);
			return getPageCount(totalJobs, JOBS_PER_PAGE);
		} catch (IOException e) {
			log.error("Failed to parse total job, site exiting...", e);
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
	}

	@Override
	protected Exception getFailedException() {
		return exception;
	}
}
