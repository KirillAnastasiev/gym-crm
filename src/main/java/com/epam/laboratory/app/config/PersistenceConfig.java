package com.epam.laboratory.app.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@ComponentScan(basePackages = "com.epam.laboratory.app.repository")
@EnableTransactionManagement
public class PersistenceConfig {

    @Value("${datasource.url}")
    private String databaseUrl;

    @Value("${datasource.schema}")
    private String databaseSchema;

    @Value("${datasource.username}")
    private String databaseUsername;

    @Value("${datasource.password}")
    private String databasePassword;

    @Value("${datasource.driver-class-name}")
    private String databaseDriver;

    @Value("${datasource.connection-pool.maximum-size}")
    private int maximumPoolSize;

    @Value("${persistence.unit-name}")
    private String persistenceUnitName;

    @Value("${persistence.show-sql}")
    private String showSql;

    @Value("${persistence.format-sql}")
    private String formatSql;

    @Value("${persistence.generate-ddl}")
    private boolean generateDdl;

    @Value("${persistence.database-platform}")
    private String database;

    @Bean
    public DataSource dataSource() {
        HikariConfig hc = new HikariConfig();
        hc.setPoolName("HikariCP Pool");
        hc.setDriverClassName(databaseDriver);
        hc.setJdbcUrl(databaseUrl);
        hc.setSchema(databaseSchema);
        hc.setUsername(databaseUsername);
        hc.setPassword(databasePassword);
        hc.setMaximumPoolSize(maximumPoolSize);
        hc.setAutoCommit(false);
        return new HikariDataSource(hc);
    }

    @Bean
    public DataSourceInitializer dataSourceInitializer(DataSource dataSource) {
        DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(dataSource);
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        Resource schemaScript = new ClassPathResource("schema.sql");
        Resource dataScript = new ClassPathResource("data.sql");
        populator.addScript(schemaScript);
        populator.addScript(dataScript);
        initializer.setDatabasePopulator(populator);
        return initializer;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setPersistenceUnitName(persistenceUnitName);
        emf.setDataSource(dataSource);
        emf.setPackagesToScan("com.epam.laboratory.app.domain");

        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setGenerateDdl(generateDdl);
        adapter.setDatabase(Database.valueOf(database.toUpperCase()));
        emf.setJpaVendorAdapter(adapter);
        emf.setJpaDialect(new HibernateJpaDialect());

        Properties jpaProperties = new Properties();
        jpaProperties.put("hibernate.show_sql", showSql);
        jpaProperties.put("hibernate.format_sql", formatSql);
        jpaProperties.put("hibernate.connection.characterEncoding", "UTF-8");
        jpaProperties.put("hibernate.connection.useUnicode", "true");
        emf.setJpaProperties(jpaProperties);
        return emf;
    }

    @Bean
    public JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory, DataSource dataSource) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        transactionManager.setDataSource(dataSource);
        return transactionManager;
    }

}
