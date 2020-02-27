package io.naztech.jobharvestar.crawler;

import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import io.naztech.talent.dao.SiteMetaDataRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
//@Transactional
//@Rollback(false)
//@ActiveProfiles({ "linux", "test" })
public class StartScraperFromQueueTest {
	@Autowired SiteMetaDataRepository siteRepo;
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testExecuteScraper() {
		fail("Not yet implemented");
	}

	@Test
	public void testStartScraperInProgressByHostName() {
		StartScraperFromQueue ob = new StartScraperFromQueue();
		System.out.println(ob.getHostname());
		if(siteRepo == null) System.out.println("siteRepo is null");
	//	siteRepo.findAll();
		List<String> siteList = siteRepo.findInProgressByHost(ob.getHostname());
		System.out.println(siteList.toString());
	}

}
