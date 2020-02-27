package io.naztech.jobharvestar.dao;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import io.naztech.jobharvestar.service.JobService;
import io.naztech.talent.dao.JobRepository;
import io.naztech.talent.model.Job;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Rollback(false)
@ActiveProfiles({ "linux", "test" })
public class JobRepositoryTest {

	@Autowired
	private JobRepository repo;
	@Autowired
	private JobService jServ;
	
	@Autowired private AmqpTemplate amqp;

	@Test
	public void testSendScraper() {
		amqp.convertAndSend("scraper-exchange", "scraper-queue", "Jpmorgan");
	}

	@Test
	public void testSaveAll() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindById() {
		Optional<Job> opt = repo.findById(100003L);
		assertTrue(opt.isPresent());
		System.out.println(opt.get());
	}

	@Test
	public void testFindByUrl() {
		Job ob = repo.findByUrl("http://mysite.com/jobs/ext1234");
		System.out.println(ob);
		assertNotNull(ob);
		assertThat(ob.getReferenceId(), is("EXT1234"));
	}
	
	@Test
	public void testJobProcessed() {
		//assertEquals(repo.findByUrl("http://mysite.com/jobs/ext1234").getTagCount(),-32768);
		System.out.println(repo.findByUrl("http://mysite.com/jobs/ext1234").getTagCount());
	}

	@Test
	public void testFindAll() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindLimited() {
		List<Job> list = repo.findAll(31, 1000);
		for (Job it: list) System.out.println(it.getJobId());
	}

	@Test
	public void testSave() {
		jServ.insert(newJob(1L));
	}

	
	
	private Job newJob(long l) {
		Job ob = new Job();
		ob.setSiteMetaKey(l);
		ob.setReferenceId("EXT1234");
		ob.setType("Full-time");
		ob.setUrl("http://mysite.com/jobs/ext1234");
		ob.setLocation("Houston, TX");
		ob.setPostedDate(LocalDate.now());
		ob.setName("Senior Analyst");
		ob.setActive(false);
		ob.setTagCount((short) 0);
		ob.setRunEventKey(RandomUtils.nextInt());
		ob.setTitle("Need Senior Analyst");
		ob.setSpec("He/She will analyze data to find out important customers.");
		ob.setPrerequisite("Need to know Java, Spring framework. Can work in team. CS graduates preffered.");
		ob.setCategory("Analyst");
		ob.setApplicationUrl("Apply for it");
		ob.setApplyEmail("i@a.com");
		ob.setComment("Added from JUnit");
		ob.setUserModId(10001);
		return ob;
	}
}
