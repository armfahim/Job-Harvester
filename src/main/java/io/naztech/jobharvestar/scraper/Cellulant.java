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
 * Cellulent jobs site parse<br>
 * URL: https://www.cellulant.com/jobs/
 * 
 * @author Kowshik Saha
 * @author iftekar.alam
 * @since 2019-04-02
 */
@Slf4j
@Service

public class Cellulant extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.CELLULANT;
	private static String SITE_URL = null;
	private int expectedJobCount=0;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		Document doc=Jsoup.connect(site.getUrl()).get();
		Elements rowList = doc.select("div[class=et_pb_section et_pb_section_0 et_pb_with_background et_section_regular]>div");
		for (int i = 2; i < rowList.size(); i+=2) {
			String url=rowList.get(i).getElementsByTag("div").get(0).getElementsByTag("div").get(0).select("div>p>a").attr("href");
			expectedJobCount+=1;
			Job job=new Job(url);
			try {
				saveJob(getJobDetails(job), site);
			} catch(Exception e) {
				exception = e;
				log.warn("Failed to parse job details of " + job.getUrl(), e);
			}
		}
	}

	private Job getJobDetails(Job job) throws IOException, PageScrapingInterruptedException {
		Document doc=Jsoup.connect(job.getUrl()).get();
		Elements spec = doc.select("div[class=et_pb_column et_pb_column_2_3  et_pb_column_0 et_pb_css_mix_blend_mode_passthrough]>div");
		for (int i = 2; i < spec.size(); i++) {
			if (job.getSpec() != null) job.setSpec(job.getSpec() + "\n" + spec.get(i).text().trim());
			else job.setSpec(spec.get(i).text().trim());
		}
		job.setTitle(spec.get(0).select("h2").text().trim());
		job.setName(job.getTitle());
		Elements dec = doc.select("div[class=et_pb_text_inner]>h4");
		for (Element el : dec) {
			if(el.text().contains("Location")) job.setLocation(el.nextElementSibling().text());
			if(el.text().contains("Department")) job.setCategory(el.nextElementSibling().text());
			if(el.text().contains("Type")) job.setType(el.nextElementSibling().text().split("permanent")[0].trim());
		}
		job.setApplicationUrl(doc.selectFirst("a[class=et_pb_button et_pb_custom_button_icon  et_pb_button_1 et_pb_module et_pb_bg_layout_light]").attr("href"));
		return job;
	}

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return SITE_URL.substring(0, 25);
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
