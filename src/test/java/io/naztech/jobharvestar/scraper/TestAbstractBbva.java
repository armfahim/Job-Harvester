package io.naztech.jobharvestar.scraper;

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
 * @author jannatul.maowa
 * @since 2019-06-12
 */


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.BeforeClass;
import org.junit.Test;


public class TestAbstractBbva extends TestAbstractScrapper{

	//private static WebClient client;
	private static Document doc;
	static String url ="https://careers.bbva.com/compass/offer/2019-121547/sales-service-advisor-personal-banker-teller-beaumont-central/";
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		doc = Jsoup.connect(url).get();
	}

	@Test
	public  void testJobDetails(){
		Elements jobE = doc.select("ul[class=lista_datosOferta]>li");
		System.out.println(jobE.size());
		for(int i=0;i<jobE.size();i++)
			{
			   if(jobE.get(i).text().contains("Location")||jobE.get(i).text().contains("Published on"))
					
				{
					System.out.println(jobE.get(i).text().split(":")[1].trim());
				}
			}
		 jobE = doc.select("a[id=btninscribete]"); 
		 System.out.println(jobE.get(0).attr("href"));
		 Element job=doc.selectFirst("div[class=moduloContenido_ofertaDetalle_descripcion]");
		 System.out.println(job.text());
		 job = doc.selectFirst("h1[class=pagina_titulo]");
		 System.out.println(job.text().split("-")[0]);
	}	
}
