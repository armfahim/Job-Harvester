package io.naztech.jobharvestar.dao;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.time.LocalDate;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.jobharvestar.service.JobService;
import io.naztech.jobharvestar.service.ScraperFinderService;
import io.naztech.talent.dao.OrganizationRepository;
import io.naztech.talent.dao.SiteMetaDataRepository;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.Organization;
import io.naztech.talent.model.SiteMetaData;

@RunWith(SpringRunner.class)
@SpringBootTest
@ComponentScan(basePackages = { "io.naztech.jobharvestar", "io.naztech.jobharvestar.dao"},
							excludeFilters = @ComponentScan.Filter(pattern = "io.naztech.jobharvestar.scraper"))
@ActiveProfiles({ "linux", "test" })
public class JobServiceTest {
	@Autowired
	private OrganizationRepository orgRepo;
	@Autowired
	private SiteMetaDataRepository metaRepo;

	@Autowired
	JobService jobService;
	
	@Autowired
	ScraperFinderService scraperFinder;

	@Before
	public void setUp() throws Exception {
	}
	
	@Test
	public void testScraperInstance() {
		assertNotNull(scraperFinder.getScrapperInstance("jpmorgan"));
	}

	@Test
	public void testSave() {
		Organization org = orgRepo.findByShortName("JPM");
		//Job ob = repo.save(newJob(metaRepo.findByOrganizationKey(org.getKey())));
		Job ob = jobService.insert(newJob(metaRepo.findByOrganizationKey(org.getKey())));
		System.out.println(ob);
		//assertTrue(ob.getKey() > 0);
	}
	
	@Test
	public void testRunEvent() throws Exception {
		Integer val = (Integer) jobService.getRunEventKey("JPM");
		System.out.println(val);
		assertNotNull(val);
	}

	private Job newJob(SiteMetaData meta) {
		Job ob = new Job();
		ob.setSiteMetaKey(meta.getKey());
		ob.setReferenceId("EXT1234");
		ob.setType("Full-time");
		ob.setUrl("http://mysite.com/jobs/ext1234");
		ob.setLocation("Houston, AB");
		ob.setPostedDate(LocalDate.now());
		ob.setRunEventKey(102022);
		ob.setName("Senior Analyst");
		ob.setTitle("Need Senior Analyst");
		ob.setSpec("He/She will analyze data to find out important customers.");
		ob.setPrerequisite("Need to know Java, Spring framework. Can work in team. CS graduates preffered.");
		ob.setCategory("Analyst");
		ob.setApplicationUrl("Apply for it");
		ob.setApplyEmail("i@a.com");
		ob.setComment("Added from JUnit");
		ob.setUserModId(10001);
		ob.setTagCount((short) 239);
		ob.setOrgShortName(meta.getOrgShortName());
		ob.onInsert();
		return ob;
	}

	@Test
	public void testFindById() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindAllForOrg() {
		Organization org = orgRepo.findByShortName(ShortName.MUFG_INDONESIA);
		System.out.println(org);
		SiteMetaData site = metaRepo.findByOrganizationKey(org.getKey());
		System.out.println(site);
		System.out.println(site.getKey());
		System.out.println("==================");

		Job criteria = new Job();
		criteria.setSiteMetaKey(site.getKey());
		criteria.setUserModId(100001);
		List<Job> list = jobService.select(criteria);
		assertNotNull(list);
		for (Job it : list) {
			System.out.println(it.getTitle());
			System.out.println(it.getPrerequisite());
			System.out.println(it.getSpec());
		}
	}

	@Test
	public void testFindJobByUrl() {
//		List<Job> list = repo.findJobByUrl("http://careers.nab.com.au/aw/en/job/740696/senior-business-banking-manager-caloundra");
//		for (Job ob : list) System.out.println(ob);
	}

	@Test
	public void testDelete() {
		fail("Not yet implemented");
	}

}
