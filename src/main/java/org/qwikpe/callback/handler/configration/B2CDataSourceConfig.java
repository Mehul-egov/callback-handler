package org.qwikpe.callback.handler.configration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJpaRepositories(
        basePackages = "org.qwikpe.callback.handler.repository.b2c",
        entityManagerFactoryRef = "b2cEntityManagerFactory",
        transactionManagerRef = "b2cTransactionManager"
)
public class B2CDataSourceConfig {

    @Autowired
    private Environment env;

    @Bean(name = "b2cDataSource")
    public DataSource b2bDataSource() {

        DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
        driverManagerDataSource.setUrl(env.getProperty("b2c.datasource.url"));
        driverManagerDataSource.setUsername(env.getProperty("b2c.datasource.username"));
        driverManagerDataSource.setPassword(env.getProperty("b2c.datasource.password"));
        driverManagerDataSource.setDriverClassName(env.getProperty("b2c.datasource.driver-class-name"));
        return driverManagerDataSource;
    }

    @Bean(name = "b2cEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean b2cEntityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(b2bDataSource());
        em.setPackagesToScan("org.qwikpe.callback.handler.domain.b2c");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.dialect", env.getProperty("b2c.datasource.hibernate.dialect"));
        em.setJpaPropertyMap(properties);

        return em;
    }

    @Bean(name = "b2cTransactionManager")
    public PlatformTransactionManager b2cTransactionManager(
            @Qualifier("b2cEntityManagerFactory") LocalContainerEntityManagerFactoryBean b2cEntityManagerFactory) {
        return new JpaTransactionManager(b2cEntityManagerFactory.getObject());
    }
}
