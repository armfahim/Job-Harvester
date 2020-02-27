package io.naztech.jobharvestar.scraper.glassdoor;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.jobharvestar.scraper.glassdoor.AbstractGlassDoor;

/**
 * Fair job site scrapper<br>
 * Url: https://www.glassdoor.com/Jobs/FAIR-Jobs-E1787620.htm
 * 
 * @author Shadman Shahriar
 * @author tanmoy.tushar
 * @author bm.alamin
 * @since 2019-03-24
 */
@Service
//@Slf4j
public class Fair extends AbstractGlassDoor {
	public static final String SITE = ShortName.FAIR;
	/*
	 * private String baseUrl; private int expectedJobCount = 0; private Exception
	 * exception;
	 */
	/*
	 * @Override public void scrapJobs() throws Exception {
	 * startSiteScrapping(getSiteMetaData(getSiteName())); }
	 * 
	 * @Override public void getScrapedJobs(SiteMetaData siteMeta) throws
	 * IOException, InterruptedException { this.baseUrl =
	 * siteMeta.getUrl().substring(0, 20); Document document =
	 * Jsoup.connect(siteMeta.getUrl()).get(); Elements jobDept =
	 * document.select("div[class=department__position___FrS0L]>a"); List<String>
	 * jobCategory = new ArrayList<String>(); for (Element element : jobDept) { if
	 * (isStopped()) throw new PageScrapingInterruptedException();
	 * jobCategory.add(getBaseUrl() + element.attr("href").trim()); }
	 * browseJobList(jobCategory, siteMeta); }
	 * 
	 * private void browseJobList(List<String> jobCategory, SiteMetaData siteMeta)
	 * throws IOException, PageScrapingInterruptedException { for (String url :
	 * jobCategory) { if (isStopped()) throw new PageScrapingInterruptedException();
	 * Document document = Jsoup.connect(url).get(); String category =
	 * document.selectFirst("h1").text().trim(); Elements jobUrl =
	 * document.getElementsByClass("position-item___22Tpy"); expectedJobCount +=
	 * jobUrl.size(); for (Element element : jobUrl) { if (isStopped()) throw new
	 * PageScrapingInterruptedException(); Job job = new Job(getBaseUrl() +
	 * element.getElementsByTag("a").attr("href").trim());
	 * job.setTitle(element.getElementsByTag("a").text().trim());
	 * job.setName(job.getTitle()); job.setCategory(category);
	 * job.setLocation(element.getElementsByTag("div").text().trim()); try {
	 * saveJob(getJobDetails(job), siteMeta); } catch (Exception e) { exception = e;
	 * log.warn("Failed to parse job detail of " + job.getUrl(), e); } } } }
	 * 
	 * private Job getJobDetails(Job job) throws IOException { Document document =
	 * Jsoup.connect(job.getUrl()).get(); Element apply =
	 * document.getElementById("applyButton"); job.setApplicationUrl(getBaseUrl() +
	 * apply.attr("href").trim());
	 * job.setSpec(document.selectFirst("div[class=description___2n3vr]").text().
	 * trim()); return job; }
	 */

	@Override
	public String getSiteName() {
		return SITE;
	}

	/*
	 * @Override protected String getBaseUrl() { return baseUrl; }
	 * 
	 * @Override protected int getExpectedJob() { return expectedJobCount; }
	 * 
	 * @Override protected void destroy() { }
	 * 
	 * @Override protected Exception getFailedException() { return exception; }
	 */
}
