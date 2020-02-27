package io.naztech.jobharvestar.dao;

import static org.junit.Assert.fail;

import java.time.LocalDateTime;
import java.util.Arrays;

import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.env.Environment;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.dao.OrganizationRepository;
import io.naztech.talent.dao.SiteMetaDataRepository;
import io.naztech.talent.model.Organization;
import io.naztech.talent.model.SiteMetaData;

@RunWith(SpringRunner.class)
@SpringBootTest
@ComponentScan(
	basePackages = { "io.naztech.jobharvestar.service", "io.naztech.jobharvestar.model" },
	basePackageClasses = { io.naztech.jobharvestar.config.DatabaseConfig.class },
	excludeFilters = @ComponentScan.Filter(type = FilterType.ASPECTJ,
		pattern = {"io.naztech.jobharvestar.scraper.*", "io.naztech.jobharvestar.crawler.*"}))
@Transactional
@Rollback(false)
@ActiveProfiles({ "linux", "test" })
public class SiteMetaDataRepositoryTest {
	@Autowired
	private Environment environment;

	@Autowired
	private OrganizationRepository orgRepo;

	@Autowired
	private SiteMetaDataRepository repo;

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testSave() {
		Organization org = orgRepo.findByShortName("DUMMY");
		SiteMetaData ob = repo.save(newSiteMetaData(org));
		System.out.println(ob);
	}

	private SiteMetaData newSiteMetaData(Organization org) {
		SiteMetaData ob = new SiteMetaData();
		ob.setOrganizationKey(org.getKey());
		ob.setUrl("http://jobs.dummy.org");
		ob.setComment("Dummy site config set for testing purpose, please ignore");
		LocalDateTime now = LocalDateTime.now();
		ob.setLastRun(now);
		ob.setLastRunStart(now);
		ob.setLastRunEnd(now);
		ob.setLastRunStatus("OK");
		return ob;
	}

	@Test
	public void testUpdate() {
		SiteMetaData ob = repo.findByOrganizationKey(orgRepo.findByShortName(ShortName.CANADIAN_IMPERIAL_BANK).getKey());
		System.out.println(ob.getUrl());
	}

	@Test
	public void testFindById() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindAll() {
		Iterable<SiteMetaData> list = repo.findAll();
		for (SiteMetaData it : list) System.out.println(it);
	}

	@Test
	public void testDelete() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindSiteMeta() {
		Organization org = orgRepo.findByShortName(ShortName.ALLIANZ);
		System.out.println(org);
		SiteMetaData site = repo.findByOrganizationKey(org.getKey());
		System.out.println(site.getUrl());
	}

	@Test
	public void testIsSiteRunning() {
		System.out.println("ACTIVE profiles are : " + Arrays.toString(environment.getActiveProfiles()));
		SiteMetaData site = repo.findByOrgShortName(ShortName.JPMORGAN_CHASE_N_CO);
		System.out.println("Last run: " + site.getLastRun() + "; started: " + site.getLastRunStart() + "; Stopped: " + site.getLastRunEnd());
		//System.out.println(AbstractScraper.isSiteRunning(site));
		//assertTrue(AbstractScraper.isSiteRunning(site));
	}
	

	@Test
	public void testAddScraper() {
		SiteMetaData org = repo.findByOrgShortName("JPM");
		if (org.getScraper().equals("?")) {
			org.setScraper("jpmorgan");
			repo.save(org);
			System.out.println("Saved Scraper: "+org.getScraper());
		}
	}
}
