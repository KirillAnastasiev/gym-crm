package com.epam.laboratory.app.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
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

@Configuration
@ComponentScan(basePackages = "com.epam.laboratory.app")
@PropertySource("classpath:application.properties")
@EnableAspectJAutoProxy
@EnableTransactionManagement
public class AppConfig {

    @Value("${datasource.url}")
    private String databaseUrl;

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
    private boolean showSql;

    @Value("${persistence.generate-ddl}")
    private boolean generateDdl;

    @Value("${persistence.database-platform}")
    private String database;

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public static JsonMapper objectMapper() {
        return JsonMapper.builder()
                .findAndAddModules()
                .configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, false)
                .configure(MapperFeature.SORT_CREATOR_PROPERTIES_BY_DECLARATION_ORDER, true)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .enable(SerializationFeature.INDENT_OUTPUT)
                .enable(SerializationFeature.FLUSH_AFTER_WRITE_VALUE)
                .enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)
                .enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();
    }

    @Bean
    public DataSource dataSource() {
        HikariConfig hc = new HikariConfig();
        hc.setPoolName("HikariCP Pool");
        hc.setDriverClassName(databaseDriver);
        hc.setJdbcUrl(databaseUrl);
        hc.setUsername(databaseUsername);
        hc.setPassword(databasePassword);
        hc.setMaximumPoolSize(maximumPoolSize);
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
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setPersistenceUnitName(persistenceUnitName);
        emf.setDataSource(dataSource);
        emf.setPackagesToScan("com.epam.laboratory.app.domain");
        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setShowSql(showSql);
        adapter.setGenerateDdl(generateDdl);
        adapter.setDatabase(Database.valueOf(database.toUpperCase()));
        emf.setJpaVendorAdapter(adapter);
        emf.setJpaDialect(new HibernateJpaDialect());
        return emf;
    }

    @Bean
    public JpaTransactionManager transactionManager(LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory.getObject());
        return transactionManager;
    }
}
