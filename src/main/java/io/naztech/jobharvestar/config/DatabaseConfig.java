package io.naztech.jobharvestar.config;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import com.nazdaqTechnologies.jdbc.JdbcService;
import com.nazdaqTechnologies.jdbc.JdbcStatementFactory;

/**
 * Database configuration for Spring framework DI. <br>
 * No more Spring XML configuration file needed.
 * 
 * @author Imtiaz Rahi
 * @since 2019-01-21
 */
@Configuration
@PropertySource(value = { "classpath:db.properties", "file:${DB_PROPERTIES}" }, ignoreResourceNotFound = true)
public class DatabaseConfig {

	@Value("${jdbc.driver}")
	private String dbDriver;
	@Value("${jdbc.url}")
	private String dbUrl;
	@Value("${jdbc.username}")
	private String dbUser;
	@Value("${jdbc.password}")
	private String dbPass;

	@Bean
	public JdbcService jdbcService(DataSource dataSource) {
		JdbcService ob = new JdbcService();
		ob.setDataSource(dataSource);
		ob.setTransactionManager(txManager(dataSource));
		ob.setJdbcStatementFactory(jdbcStatetementFactory());
		return ob;
	}

	public JdbcStatementFactory jdbcStatetementFactory() {
		return new JdbcStatementFactory();
	}

	@Bean
	public DataSource dataSource() {
		BasicDataSource ds = new BasicDataSource();
		ds.setDriverClassName(dbDriver);
		ds.setUrl(dbUrl);
		ds.setUsername(dbUser);
		ds.setPassword(dbPass);
		ds.setInitialSize(5);
		ds.setMaxActive(-1);
		ds.setMaxIdle(10);
		ds.setDefaultAutoCommit(true);
		return ds;
	}

	/* Used by Naztech JDBC */
	@Bean
	public DataSourceTransactionManager txManager(DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setDataSource(dataSource());
		em.setPackagesToScan("io.naztech.jobharvestar", "io.naztech.talent.model");

		JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		em.setJpaVendorAdapter(vendorAdapter);
		em.setJpaProperties(hibernateProps());
		return em;
	}

	Properties hibernateProps() {
		Properties props = new Properties();
		props.setProperty("default_schema", "dbo");
		props.setProperty("hibernate.dialect", "org.hibernate.dialect.SQLServer2012Dialect");
		return props;
	}

	/* Used by Spring JPA */
	@Bean
	@Primary
	public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
		return new JpaTransactionManager(emf);
	}
}
