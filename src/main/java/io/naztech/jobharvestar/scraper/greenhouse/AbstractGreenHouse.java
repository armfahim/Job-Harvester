package io.naztech.jobharvestar.scraper.greenhouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.scraper.AbstractScraper;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;

/**
 * All Jobsites of
 * <ul>
 * <li><a href="https://www.acorns.com/careers/">Acorns</a></li>
 * <li><a href=
 * "https://boards.greenhouse.io/embed/job_board?for=allbirds&b=https%3A%2F%2Fwww.allbirds.com%2Fpages%2Fcareers">AllBirds</a></li>
 * <li><a href=
 * "https://boards.greenhouse.io/aqr#.WtpYtIyPLup">AqrCapital</a></li>
 * <li><a href="https://boards.greenhouse.io/behavox">Behavox</a></li>
 * <li><a href="https://branch.io/careers/jobs/">Branch</a></li>
 * <li><a href="https://boards.greenhouse.io/bridgewater89">BridgeWater</a></li>
 * <li><a href="https://boards.greenhouse.io/careem">CareemNewworks</a></li>
 * <li><a href="https://boards.greenhouse.io/chainalysis">Chainalysis</a></li>
 * <li><a href="https://boards.greenhouse.io/circle">Circle</a></li>
 * <li><a href="https://boards.greenhouse.io/coupang">Coupang</a></li>
 * <li><a href="https://boards.greenhouse.io/desktopmetal">DeskTopMetal</a></li>
 * <li><a href="https://boards.greenhouse.io/doordash">DoorDash</a></li>
 * <li><a href="https://boards.greenhouse.io/guideline">Guideline</a></li>
 * <li><a href="https://boards.greenhouse.io/gusto">Gusto</a></li>
 * <li><a href=
 * "https://boards.greenhouse.io/embed/job_board?for=illumio&b=https%3A%2F%2Fwww.illumio.com%2Fcareer-openings">Illumio</a></li>
 * <li><a href="https://www.indigoag.com/join-us#openings">Indigo</a></li>
 * <li><a href=
 * "https://boards.greenhouse.io/embed/job_board?for=infinidat&b=https%3A%2F%2Fhiring.infinidat.com%2F">Infinidat</a></li>
 * <li><a href="https://careers.instacart.com/openings/">Instacart</a></li>
 * <li><a href=
 * "https://www.intarcia.com/about/careers.html">IntarciaTherapeutics</a></li>
 * <li><a href="https://www.intercom.com/careers#roles">Intercom</a></li>
 * <li><a href="https://www.jumo.world/careers"><a/></li>
 * <li><a href="https://justworks.com/careers/all-positions">JustWork</a></li>
 * <li><a href="https://www.lendinghome.com/careers">LendingHome</a></li>
 * <li><a href=
 * "https://boards.greenhouse.io/embed/job_board?for=lendup&b=https%3A%2F%2Fwww.lendup.com%2Fcareers%2Fpositions">LendUp</a></li>
 * <li><a href=
 * "https://boards.greenhouse.io/embed/job_board?for=iex&b=https%3A%2F%2Fiextrading.com%2Fcareers%2F">Lex</a></li>
 * <li><a href=
 * "https://boards.greenhouse.io/embed/job_board?for=particle&b=https%3A%2F%2Fwww.particle.io%2Fjobs%2F">Particle</a></li>
 * <li><a href="https://boards.greenhouse.io/qualtrics">Qualtrics</a></li>
 * <li><a href="https://www.raise.me/jobs#open-positions">RaiseMe</a></li>
 * <li><a href=
 * "https://boards.greenhouse.io/embed/job_board?for=robinhood&b=https%3A%2F%2Fcareers.robinhood.com%2Fopenings">Robinhood</a></li>
 * <li><a href=
 * "https://boards.greenhouse.io/embed/job_board?for=roostify&b=https%3A%2F%2Fwww.roostify.com%2Fopen-positions">Roostify</a></li>
 * <li><a href=
 * "https://boards.greenhouse.io/rubiconglobal">RubiconGlobal</a></li>
 * <li><a href=
 * "https://boards.greenhouse.io/embed/job_board?for=sfox&b=https%3A%2F%2Fwww.sfox.com%2Fjobs.html">Sfox</a></li>
 * <li><a href=
 * "https://www.sigfig.com/site/#/jobs#positions-section">Sigfig</a></li>
 * <li><a href=
 * "https://boards.greenhouse.io/soundhoundinc">SoundHoundinc</a></li>
 * <li><a href=
 * "https://boards.greenhouse.io/embed/job_board?for=stashinvest&b=https%3A%2F%2Fwww.stashinvest.com%2Fcareers">Stash</a></li>
 * <li><a href="https://boards.greenhouse.io/sweetgreen">Sweetgreen</a></li>
 * <li><a href="https://boards.greenhouse.io/tripactions">TripActions</a></li>
 * <li><a href="https://boards.greenhouse.io/uptake">Uptake</a></li>
 * <li><a href=
 * "https://boards.greenhouse.io/vice#.WbFWJRNSz-Z">ViceMedia</a></li>
 * <li><a href="https://www.zumepizza.com/careers">ZumePizza</a></li>
 * </ul>
 * 
 * @author naym.hossain
 * @author assaduzzaman.sohan
 * @author kamrul.islam
 * @author fahim.reza
 * @since 2019-03-04
 */
@Service
public abstract class AbstractGreenHouse extends AbstractScraper implements Scrapper {
	protected final Logger log = LoggerFactory.getLogger(getClass());

	protected String baseUrl;
	protected int expectedJobCount;
	protected Exception exception;

	private static ChromeDriver driver;
	private static WebDriverWait wait;

	protected boolean useDriver;
	protected boolean useDriverDetailPage;
	protected boolean addBaseUrl;
	protected boolean iframeInDetailPage;
	protected String filterParm = "/jobs/";

	@Override
	public void scrapJobs() throws Exception {
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(80, TimeUnit.SECONDS);
		SiteMetaData site = getSiteMetaData(getSiteName());
		if (log.isTraceEnabled())
			log.trace(site.getUrl());
		setBaseUrl(site);
		startSiteScrapping(site);
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		driver.get(siteMeta.getUrl());
		wait = new WebDriverWait(driver, 70);
		getSummaryPages(siteMeta);
	}

	protected void getSummaryPages(SiteMetaData siteMeta) throws IOException, InterruptedException {
		List<String> allJobLink = getAllJobLink(driver, wait);
		expectedJobCount = allJobLink.size();
		for (int i = 0; i < allJobLink.size(); i++) {
			if (isStopped())
				throw new PageScrapingInterruptedException();
			Job job = new Job();
			if (iframeInDetailPage) {
				driver.get(allJobLink.get(i));
				WebElement wEl = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("grnhse_iframe")));
				job.setUrl(wEl.getAttribute("src"));
			} else
				job.setUrl(allJobLink.get(i));

			try {
				saveJob(getJobDetail(job), siteMeta);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of " + job.getUrl(), e);
			}
		}
	}

	protected List<String> getAllJobLink(ChromeDriver driver, WebDriverWait wait) {
		List<String> allJobLink = new ArrayList<>();
		List<WebElement> list = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@class='opening']/a")));
		for (int i = 0; i < list.size(); i++) {
			String link = list.get(i).getAttribute("href");
			if (link == null)
				continue;
			if (link.contains(filterParm)) {
				if (addBaseUrl)
					allJobLink.add(baseUrl + link);
				else
					allJobLink.add(link);
			} else {
				allJobLink.add(link);
			}
		}
		return allJobLink;
	}

	protected Job getJobDetail(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).get();
		job.setTitle(doc.select("div > h1.app-title").get(0).text().trim());
		job.setName(job.getTitle());
		job.setLocation(doc.select("div.location").get(0).text().trim());
		job.setSpec(doc.select("div#content").get(0).text().trim());
		return job;
	}

	@Override
	protected int getExpectedJob() {
		return expectedJobCount;
	}

	@Override
	protected void destroy() {
		driver.quit();
	}

	@Override
	protected Exception getFailedException() {
		return exception;
	}

	protected abstract void setBaseUrl(SiteMetaData site);

}
