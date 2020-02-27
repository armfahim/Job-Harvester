package io.naztech.jobharvestar.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import com.nazdaqTechnologies.core.message.processor.json.gson.GsonJsonMessageProcessor;
import com.nazdaqTechnologies.core.service.ServiceMap;
import com.nazdaqTechnologies.jdbc.JdbcService;

import io.naztech.jobharvestar.service.JobService;
import io.naztech.jobharvestar.service.ServiceCoordinator;

/**
 * Configure NazTech JDBC to use with Spring Boot.
 * 
 * @author md.kamruzzaman
 * @author Mahmud Rana
 */
@Configuration
public class NaztechJdbcConfig {

	@Autowired
	private JobService jobService;

	@Bean("ServiceCoordinator")
	ServiceCoordinator serviceCoordinator() {
		ServiceCoordinator sc = new ServiceCoordinator();
		sc.setServiceMap(serviceMap());
		return sc;
	}

	@Bean
	ServiceMap serviceMap() {
		ServiceMap ob = new ServiceMap();
		ob.addService(jobService);
		return ob;
	}

	@Bean("jobService")
	public JobService jobService(JdbcService jdbcService) {
		JobService ob = new JobService();
		ob.setJdbcService(jdbcService);
		return ob;
	}
	
	@Bean
	public GsonJsonMessageProcessor gsonJsonMessageProcessor() {
		GsonJsonMessageProcessor ob = new GsonJsonMessageProcessor();
		ob.getClassMap().put("jobService", "jobService");
		return ob;
	}

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}
}
