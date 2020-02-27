package io.naztech.jobharvestar.dao;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;

import com.opencsv.CSVReader;

import io.naztech.talent.dao.OrganizationRepository;
import io.naztech.talent.dao.SiteMetaDataRepository;
import io.naztech.talent.model.Organization;
import io.naztech.talent.model.SiteMetaData;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RunWith(SpringRunner.class)
@SpringBootTest
@ComponentScan(basePackages = { "io.naztech.jobharvestar", "io.naztech.jobharvestar.service",
		"io.naztech.jobharvestar.model" }, excludeFilters = @ComponentScan.Filter(pattern = "io.naztech.jobharvestar.scraper"))
@Transactional
@Rollback(false)
public class UploadSiteMetaData {

	@Autowired
	private OrganizationRepository orgRepo;

	@Autowired
	private SiteMetaDataRepository repo;

	@Before
	public void setUp() throws Exception {
	}

	@Test @Ignore
	public void uploadFromCsv() throws IllegalStateException, IOException {
		try (InputStream stream = getClassLoader().getResourceAsStream("wave-c-site-config.csv");
				CSVReader reader = new CSVReader(new InputStreamReader(stream));) {

			List<String[]> list = reader.readAll();
			for (String[] it : list) {
				if (StringUtils.isEmpty(it[1]) || "?".equals(it[1])) continue;
				// System.out.println(Arrays.toString(it));
				Organization org = orgRepo.findByShortName(it[0]);
				if (org == null) continue;
				if (repo.findByUrl(it[1]) != null) continue;

				SiteMetaData ob = new SiteMetaData();
				ob.setOrganizationKey(org.getKey());
				ob.setOrgShortName(it[0]);
				ob.setComment(org.getName());
				ob.setUrl(it[1]);
				repo.save(ob);
			}
		}
	}

	private ClassLoader getClassLoader() {
		ClassLoader cl = null;
		try {
			cl = Thread.currentThread().getContextClassLoader();
		} catch (SecurityException ex) {
		}

		if (cl == null) cl = ClassLoader.getSystemClassLoader();
		return cl;
	}

	@Data @NoArgsConstructor @RequiredArgsConstructor
	public static class SiteConfig {
		@NonNull String name, url;
	}
}
