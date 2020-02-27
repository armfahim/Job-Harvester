package io.naztech.jobharvestar.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import io.naztech.talent.dao.OrganizationRepository;
import io.naztech.talent.model.Organization;

@RunWith(SpringRunner.class)
@SpringBootTest
@ComponentScan(basePackages = { "io.naztech.jobharvestar", "io.naztech.jobharvestar.service",
		"io.naztech.jobharvestar.model" }, excludeFilters = @ComponentScan.Filter(pattern = "io.naztech.jobharvestar.scraper"))
@ActiveProfiles({ "linux", "test" })
public class OrganizationRepositoryTest {

	@Autowired
	private OrganizationRepository repo;

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testSave() {
		Organization ob = new Organization();
		ob.setName("Dummy Organization");
		ob.setShortName("DUMMY");
		ob.setAddress("Bogus, XX");
		ob.setComment("Dummy organization set for testing purpose, please ignore");
		ob.setUrl("http://dummy.org");
		ob = repo.save(ob);
		System.out.println(ob);
	}

	@Test
	public void testFindById() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindByShortName() {
		Organization ob = repo.findByShortName("JPM");
		assertNotNull(ob);
		assertEquals("JPMorgan Chase & Co.", ob.getName());
	}

	@Test
	public void testFindAll() {
		Iterable<Organization> list = repo.findAll();
		for (Organization it : list)
			System.out.println(it);
	}

	@Test
	public void testDelete() {
		fail("Not yet implemented");
	}
	

}
