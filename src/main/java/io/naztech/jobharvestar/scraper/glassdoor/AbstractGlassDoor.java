package io.naztech.jobharvestar.scraper.glassdoor;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.http.conn.HttpHostConnectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.ProxyConfig;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.scraper.AbstractScraper;
import io.naztech.jobharvestar.utils.ProxyFromTextFile;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;

/**
 * All job site of GlassDoor parsing class. <br>
 * <ul>
 * <li><a href=
 * "https://www.glassdoor.com/Jobs/Voya-Financial-Jobs-E816322.htm">Voya
 * Financial</a>
 * <li><a href=
 * "https://www.glassdoor.com/Jobs/T-and-D-Holdings-Jobs-E35102.htm">T-and-D-Holdings</a>
 * <li><a href="https://www.glassdoor.com/Job/3i-group-jobs-SRCH_KE0,8.htm">3I
 * GROUP</a>
 * <li><a href=
 * "https://www.glassdoor.com/Job/sumitomo-mitsui-trust-bank-jobs-SRCH_KE0,26.htm">SUMITOMO
 * MITSUI TRUST</a>
 * <li><a href=
 * "https://www.glassdoor.com/Jobs/Tokio-Marine-Holdings-Jobs-E3481.htm">TOKIO
 * MARINE HOLDINGS</a>
 * <li><a href=
 * "https://www.glassdoor.com/Jobs/MS-and-AD-Holdings-Jobs-E354600.htm">MS&AD
 * INSURANCE GROUP</a>
 * <li><a href=
 * "https://www.glassdoor.com/Jobs/Bank-Of-China-Hong-Kong-Jobs-E16013.htm">BOC
 * HONG KONG HOLDINGS</a>
 * <li><a href=
 * "https://www.glassdoor.com/Jobs/Comerica-Jobs-E1281.htm">COMERICA</a>
 * <li><a href=
 * "https://www.glassdoor.com/Jobs/Mitsubishi-UFJ-Jobs-E40377.htm">MITSUBISHI
 * UFJ LEASE FIN</a>
 * <li><a href=
 * "https://www.glassdoor.com/Jobs/Man-Group-London-Jobs-EI_IE234069.0,9_IL.10,16_IC2671300.htm">Man
 * Group</a>
 * <li><a href=
 * "https://www.glassdoor.com/Jobs/Fukuoka-Financial-Group-Jobs-E40233.htm">FUKUOKA
 * FINANCIAL GROUP</a>
 * <li><a href=
 * "https://www.glassdoor.com/Jobs/SBI-Holdings-Jobs-E305998.htm">SBI
 * HOLDINGS</a>
 * <li><a href=
 * "https://www.glassdoor.com/Jobs/The-Baupost-Group-Jobs-E260422.htm">Baupost
 * Group</a>
 * <li><a href=
 * "https://www.glassdoor.com/Jobs/Farallon-Capital-Management-Jobs-E260058.htm">Farallon
 * Capital Mgmt</a>
 * <li><a href=
 * "https://www.glassdoor.com/Jobs/Lone-Pine-Capital-Jobs-E265544.htm">Lone Pine
 * Capital </a>
 * <li><a href=
 * "https://www.glassdoor.com/Jobs/Man-Group-London-Jobs-EI_IE234069.0,9_IL.10,16_IC2671300.htm">Man
 * Group</a>
 * <li><a href=
 * "https://www.glassdoor.co.uk/Jobs/Millennium-Management-Investment-Firm-Jobs-E850344.htm">Millennium
 * Mgmt</a>
 * <li><a href=
 * "https://www.glassdoor.com/Jobs/Alphabet-Jobs-E1043369.htm">Alphabet</a>
 * <li><a href="https://www.glassdoor.com/Jobs/Apple-Jobs-E1138.htm">Apple</a>
 * </ul>
 * TODO VOYA issue: does not go after 5 page
 * 
 * @author BM Al-Amin
 * @since 2019-03-01
 */
public abstract class AbstractGlassDoor extends AbstractScraper implements Scrapper {
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	private String baseUrl;
	private static final int MAX_RELOAD_TRY = 6;
	private ProxyConfig config = null;
	private int proxyIndex = 0;
	private ProxyFromTextFile proxyFromTextFile = new ProxyFromTextFile();

	private String jobLinkPath = "//div[@class='JobsListStyles__jobListContainer gdGrid']/ul/li[@class='JobsListStyles__jobListItem']/div/div/div/div/div/a";
	private String nextBtnPath = "//div[@class='col d-flex mt mx-auto justify-content-center ']/div/ul/li[@class='PaginationStyle__next']/a";
	private WebClient client;
	private String ip;
	private int port = 0;
	private int expectedJobCount = 0;
	private Exception exception;
	private String jobUrl = null;
	private String pageUrl = null;

	@Override
	public void scrapJobs() {
		SiteMetaData site = getSiteMetaData(getSiteName());
		client = new WebClient(BrowserVersion.FIREFOX_52);
		client.getOptions().setTimeout(TIME_1M);
		client.getOptions().setJavaScriptEnabled(false);
		client.getOptions().setCssEnabled(false);
		client.getOptions().setThrowExceptionOnScriptError(false);
		client.getOptions().setUseInsecureSSL(true);
		setBaseUrl(site);
		if (log.isTraceEnabled()) log.trace(site.getUrl());
		startSiteScrapping(site);
	}

	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		try {
			
			HtmlPage page = client.getPage(site.getUrl()); // load first page
			Thread.sleep(getRandomNumber());
			HtmlElement nextBtn = page.getBody().getFirstByXPath(getNextButtonPath());
			
			do {
				if (isStopped())
					throw new PageScrapingInterruptedException();
				List<String> links = getJobLinks(page); // second page links

				expectedJobCount += links.size();

				for (String link : links) {
					if (isStopped()) throw new PageScrapingInterruptedException();
					page = client.getPage(link); // load second page
					
					
					/**
					 * Check whether we got into the job details page, if not then modify the job
					 * details URL
					 */
					if (getJobDetailElement(page) == null) {
						jobUrl = link.contains("true") ? link.replace("true", "false") : link;
						page = null;
					}
					try {
						saveJob(getJobDetail(page, jobUrl), site);
					} catch (Exception e) {
						exception = e;
					}
				}

				if (nextBtn == null) break;
				
				pageUrl = getBaseUrl() + nextBtn.getAttribute("href");
				page = client.getPage(pageUrl); // load next page
				Thread.sleep(getRandomNumber());
				
				nextBtn = page.getFirstByXPath(getNextButtonPath());
			} while (true);

		} catch (FailingHttpStatusCodeException | SocketTimeoutException e) {
			log.warn("Failed to load page", e);
		} finally {
			client.close();
		}
	}

	private List<String> getJobLinks(HtmlPage page) {
		if (page == null)
			return Collections.emptyList();
		List<HtmlElement> links = page.getBody().getByXPath(getJobLinkPath());
		return links.stream().map(e -> getBaseUrl() + e.getAttribute("href")).collect(Collectors.toList());
	}

	/**
	 * Returns {@link Job} instance with data from details page. <br>
	 * NOTE: page argument will be null when we landed in list view with side pane
	 * and job url will be a valid one. <br>
	 * Page argument will have a valid job details instance sometimes (e.g.
	 * Sumitomo) and url will be null at that time.
	 * 
	 * @param page Job details {@link HtmlPage}
	 * @param url  job details URL
	 * @return {@link Job}
	 * @throws InterruptedException
	 */
	protected Job getJobDetail(HtmlPage page, String url) throws InterruptedException {
		try {
			if (page == null)
				page = client.getPage(url);
			if (url == null)
				url = page.getUrl().toString();
			DomElement el = getJobDetailElement(page);
			if (el == null) {
				el = reloadBlockedPage(getJobDetailElement(page), url);
			}
			
			Job job = new Job(url);
			job.setTitle(el.getTextContent());
			job.setName(job.getTitle());

			el = page.getBody().getFirstByXPath("//span[@class='subtle ib']");
			job.setLocation(el.getTextContent().substring(3));

			el = page.getBody().getFirstByXPath("//span[@class='minor nowrap']");
			if (el != null)
				job.setPostedDate(parseAgoDates(el.getTextContent().trim()));

			el = page.getBody().getFirstByXPath("//div[@class='applyCTA']/a");
			if (el != null)
				job.setApplicationUrl(getBaseUrl() + el.getAttribute("href"));

			el = page.getElementById("JobDescriptionContainer");
			/**
			 * 'job spec element null'--This issue occurs with most of glassdoor sites. For
			 * this reason we will check job spec element and reload job detail page again
			 * and again until we get job spec element as not empty
			 */
			if (el == null) {
				do {
					page = client.getPage(url);
					Thread.sleep(getRandomNumber());
					el = page.getElementById("JobDescriptionContainer");
				} while (el == null);
			}
			job.setSpec(el.getTextContent().trim());
			
			el = page.getFirstByXPath("//div[@class='minor cell alignLt']");
			if (el != null) {
				DomElement el2 = page.getFirstByXPath("//div[@class='minor cell alignRt']");
				job.setCategory(el.getTextContent().trim() + "-" + el2.getTextContent().trim());
			}
			return job;
		} catch (HttpHostConnectException e) {
			reloadPage(url);
		} catch (FailingHttpStatusCodeException | IOException e) {
			log.info("Failed to get job details " + url, e);
		}
		return null;
	}

	protected DomElement getJobDetailElement(HtmlPage page) {
		return page.getBody().getFirstByXPath("//div[@class='jobViewJobTitleWrap']/h2");
	}

	private DomElement reloadBlockedPage(DomElement el, String url) throws InterruptedException {
		int reloadCount = 0;
		while (el == null) {
			reloadCount++;
			if (reloadCount == MAX_RELOAD_TRY)
				break;
			el = getJobDetailElement(reloadPage(url));
		}
		return el;
	}

	/**
	 * Sometime this site blocks by tracking cookies and sometime with device's ip
	 * address. So when we are blocked we need both to avoid blocking as clearing
	 * cookies and rotating ip address
	 */
	private HtmlPage reloadPage(String url) throws InterruptedException {
		client.getCookieManager().clearCookies();
		client.getOptions().setProxyConfig(getProxyConfig());
		Thread.sleep(TIME_5S);
		try {
			HtmlPage page = client.getPage(url);
			Thread.sleep(getRandomNumber());
			return page;
		} catch (HttpHostConnectException e) {
			reloadPage(url);
		} catch (IOException | FailingHttpStatusCodeException e) {
			log.warn("Failed to load page " + url, e);
		}
		return null;
	}

	private ProxyConfig getProxyConfig() {
		List<String> ipPorts = proxyFromTextFile.getProxyStrings("ip.txt");
		if (proxyIndex == ipPorts.size() - 1)
			proxyIndex = 0;
		ip = ipPorts.get(++proxyIndex).split(":")[0];
		port = Integer.parseInt(ipPorts.get(++proxyIndex).split(":")[1]);
		if (!"".equals(ip) && port != 0) {
			config = new ProxyConfig(ip, port);
		}
		return config;
	}

	protected void setBaseUrl(SiteMetaData site) {
		this.baseUrl = site.getUrl().substring(0, 25);
	}

	@Override
	protected String getBaseUrl() {
		return this.baseUrl;
	}

	@Override
	protected int getExpectedJob() {
		return expectedJobCount;
	}
	
	protected String getJobLinkPath() {
		return this.jobLinkPath;
	}
	
	protected String getNextButtonPath() {
		return this.nextBtnPath;
	}
	
	private int getRandomNumber() {
		return (int)(Math.random() * 9 +1);
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
