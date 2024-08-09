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
        basePackages = "org.qwikpe.callback.handler.repository.b2b",
        entityManagerFactoryRef = "b2bEntityManagerFactory",
        transactionManagerRef = "b2bTransactionManager"
)
public class B2BDataSourceConfig {

    @Autowired
    private Environment env;

    @Primary
    @Bean(name = "b2bDataSource")
    public DataSource b2bDataSource() {

        DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
        driverManagerDataSource.setUrl(env.getProperty("b2b.datasource.url"));
        driverManagerDataSource.setUsername(env.getProperty("b2b.datasource.username"));
        driverManagerDataSource.setPassword(env.getProperty("b2b.datasource.password"));
        driverManagerDataSource.setDriverClassName(env.getProperty("b2b.datasource.driver-class-name"));
        return driverManagerDataSource;
    }

    @Primary
    @Bean(name = "b2bEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean b2bEntityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(b2bDataSource());
        em.setPackagesToScan("org.qwikpe.callback.handler.domain.b2b");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.dialect", env.getProperty("b2b.datasource.hibernate.dialect"));
        em.setJpaPropertyMap(properties);

        return em;
    }

    @Primary
    @Bean(name = "b2bTransactionManager")
    public PlatformTransactionManager b2bTransactionManager(
            @Qualifier("b2bEntityManagerFactory") LocalContainerEntityManagerFactoryBean b2bEntityManagerFactory) {
        return new JpaTransactionManager(b2bEntityManagerFactory.getObject());
    }
}
