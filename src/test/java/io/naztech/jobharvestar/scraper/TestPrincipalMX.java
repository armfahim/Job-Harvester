package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import io.naztech.talent.model.Job;
import lombok.extern.slf4j.Slf4j;

/**
 * Principal Financial Grp Mx Job Site Scraper. <br>
 * URL:
 * https://www.bumeran.com.mx/empleos-busqueda-grupo-financiero-principal.html
 * 
 * @author Sanzida Hoque
 * @since 2019-12-02
 */
@Slf4j
@Service
public class TestPrincipalMX {

	
	private static final String BASE_URL = "https://www.bumeran.com.mx";
	private static final String TotalJobPath = "//div[@class='listado-empleos col-sm-9 col-md-9']/h2/strong";
	private static final String URL = "https://www.bumeran.com.mx/empleos-busqueda-grupo-financiero-principal.html";
	private static final String CATAGORY_PATH = "//div[@class='aviso_specs']/div[9]/div[2]/a";
	private static final String LOCATION_PATH = "//div[@class='aviso_specs']/div[1]/div[2]/a";
	private static final String POSTED_DATE_PATH = "//div[@class='aviso_specs']/div[3]/div[2]";
	private static WebClient webClient;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		webClient = new WebClient(BrowserVersion.CHROME);
		webClient.waitForBackgroundJavaScript(25 * 1000);
		webClient.waitForBackgroundJavaScriptStartingBefore(10 * 1000);
		webClient.getOptions().setTimeout(30 * 000);
		webClient.setJavaScriptTimeout(25 * 1000);
		webClient.getOptions().setThrowExceptionOnScriptError(false);
		webClient.getCookieManager().setCookiesEnabled(true);

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		webClient.close();
	}

	public static LocalDate DaysToDateConverter(int days) {
		Calendar cal = new GregorianCalendar();
		cal.add(Calendar.DAY_OF_MONTH, -days);
		Date sevenDaysAgo = cal.getTime();
		System.out.println(sevenDaysAgo);
		return sevenDaysAgo.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}

	@Test
	public void test() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		HtmlPage page = webClient.getPage(URL);
		HtmlElement el = page.getFirstByXPath(TotalJobPath);
		// String totalJob = ((HtmlElement)
		// htmlItem.getFirstByXPath(TotalJobPath)).asText();

		int id = Integer.parseInt(el.getTextContent());
		System.out.println(id);
		// System.out.println(page.asXml());
		List<HtmlElement> itemList = page.getByXPath("//div[@class='aviso    aviso-simple    clearfix']/div[2]/a");
		for (HtmlElement htmlItem : itemList) {
			String title = ((HtmlElement) htmlItem.getFirstByXPath("./h3[@class='titulo-aviso']")).asText();
			// System.out.println("Title" + title);

			String jobSpecificURL = BASE_URL + htmlItem.getAttribute("href");
			Job job = new Job(jobSpecificURL);
			job.setTitle(title);
			System.out.println("Title: " + job.getTitle());
			job.setUrl(jobSpecificURL);
			System.out.println("URL: " + job.getUrl());

			try {
				page = webClient.getPage(jobSpecificURL);

				HtmlElement locE = page.getFirstByXPath(LOCATION_PATH);
				job.setLocation(locE.getTextContent().trim());
				System.out.println("Location: " + job.getLocation());

				HtmlElement PostedDate = page.getFirstByXPath(POSTED_DATE_PATH);
				String StrPostedDate = PostedDate.getTextContent().replace("Publicado", "").replace("hace", "")
						.replace("días", "").trim();
				int IntPostedDate = Integer.parseInt(StrPostedDate);
				System.out.println("Posted Date: " + DaysToDateConverter(IntPostedDate));

				HtmlElement type = page.getFirstByXPath("//div[@id='tipoTrabajo']/a");
				job.setType(type.getTextContent().trim());
				System.out.println("Type: " + job.getType());

				HtmlElement category = page.getFirstByXPath(CATAGORY_PATH);
				job.setCategory(category.getTextContent().trim());
				System.out.println("Category: " + job.getCategory());

				try {
					HtmlElement spec1 = page.getFirstByXPath("//div[@class='aviso_description']");
					job.setSpec(spec1.getTextContent().trim().replaceAll("\\s{2,}", " "));
					System.out.println("spec: " + job.getSpec());
				} catch (java.lang.NullPointerException exception) {
					// Catch NullPointerExceptions.
					log.warn("NullPointerException");
				}

			} catch (FailingHttpStatusCodeException x) {
				log.warn("FailingHttpStatusCodeException" + x);
			}

			// webClient.setThrowExceptionOnFailingStatusCode(false);
		}

	}

}
