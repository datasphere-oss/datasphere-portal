

package com.huahui.datasphere.portal.configuration;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.unidata.mdm.system.configuration.AbstractConfiguration;
import org.unidata.mdm.system.util.DataSourceUtils;

/**
 * @author theseusyang
 */
@Configuration
public class CoreConfiguration extends AbstractConfiguration {

    private static final ConfigurationId ID = () -> "CORE_CONFIGURATION";

    /**
     * Custom table prefix.
     */
    public static final String UNIDATA_BATCH_JOB_BATCH_TABLE_PREFIX = "BATCH_";

    public CoreConfiguration() {
        super();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    protected ConfigurationId getId() {
        return ID;
    }

    public static ApplicationContext getApplicationContext() {
        return CONFIGURED_CONTEXT_MAP.get(ID);
    }
    /**
     * Gets a bean.
     *
     * @param <T>
     * @param beanClass the bean class
     * @return bean
     */
    public static <T> T getBean(Class<T> beanClass) {
        if (CONFIGURED_CONTEXT_MAP.containsKey(ID)) {
            return CONFIGURED_CONTEXT_MAP.get(ID).getBean(beanClass);
        }

        return null;
    }

    /**
     * Gets beans of type.
     *
     * @param <T>
     * @param beanClass the bean class
     * @return bean
     */
    public static <T> Map<String, T> getBeans(Class<T> beanClass) {
        if (CONFIGURED_CONTEXT_MAP.containsKey(ID)) {
            return CONFIGURED_CONTEXT_MAP.get(ID).getBeansOfType(beanClass);
        }

        return Collections.emptyMap();
    }

    @Bean(name = "coreDataSource")
    public DataSource coreDataSource() {
    	Properties properties = getAllPropertiesWithPrefix(CoreConfigurationConstants.PROPERTY_CORE_DATASOURCE_PREFIX, true);
    	return DataSourceUtils.newPoolingXADataSource(properties);
    }

//    @Bean("binary-data-sql")
//    public PropertiesFactoryBean binaryDataSql() {
//        final PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
//        propertiesFactoryBean.setLocation(new ClassPathResource("/db/binary-data-sql.xml"));
//        return propertiesFactoryBean;
//    }

//    @Bean("security-sql")
//    public PropertiesFactoryBean securitySql() {
//        final PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
//        propertiesFactoryBean.setLocation(new ClassPathResource("/db/security-sql.xml"));
//        return propertiesFactoryBean;
//    }

    @Bean
    public ProviderManager authenticationManager(final AuthenticationProvider authenticationProvider) {
        return new ProviderManager(Collections.singletonList(authenticationProvider));
    }

    /**
     * use this way, not     @PropertySource(name = "securitySql", value = "classpath:db/security-sql.xml" )
     * for old functionality support (like @Qualifier("security-sql") final Properties sql)
     * and for pleasure while autoset sql query by org.unidata.mdm.core.dao.@SqlQuery
     *
     * @return
     */
//    @Bean("securitySql")
//    public PropertiesFactoryBean securitySqlProperties() {
//        PropertiesFactoryBean bean = new PropertiesFactoryBean();
//        bean.setLocation(new ClassPathResource("db/security-sql.xml"));
//        return bean;
//    }

//    @Bean("job-sql")
//    public PropertiesFactoryBean jobSqlProperties() {
//        PropertiesFactoryBean bean = new PropertiesFactoryBean();
//        bean.setLocation(new ClassPathResource("db/job-sql.xml"));
//        return bean;
//    }

//    @Bean("configuration-sql")
//    public PropertiesFactoryBean configurationSqlProperties() {
//        PropertiesFactoryBean bean = new PropertiesFactoryBean();
//        bean.setLocation(new ClassPathResource("db/configuration-sql.xml"));
//        return bean;
//    }

//    @Bean("custom-storage-sql")
//    public PropertiesFactoryBean customStorageSqlProperties() {
//        PropertiesFactoryBean bean = new PropertiesFactoryBean();
//        bean.setLocation(new ClassPathResource("db/custom-storage-sql.xml"));
//        return bean;
//    }

//    @Bean("audit-sql")
//    public PropertiesFactoryBean auditSqlProperties() {
//        PropertiesFactoryBean bean = new PropertiesFactoryBean();
//        bean.setLocation(new ClassPathResource("db/audit-sql.xml"));
//        return bean;
//    }

//    @Bean("binary-data-sql")
//    public PropertiesFactoryBean binaryDataSqlProperties() {
//        PropertiesFactoryBean bean = new PropertiesFactoryBean();
//        bean.setLocation(new ClassPathResource("db/binary-data-sql.xml"));
//        return bean;
//    }

//    @Bean("storage-model-sql")
//    public PropertiesFactoryBean storageSql() {
//        final PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
//        propertiesFactoryBean.setLocation(new ClassPathResource("/db/storage-model-sql.xml"));
//        return propertiesFactoryBean;
//    }

//    @Bean("libraries-sql")
//    public PropertiesFactoryBean dqLibrariesSqlProperties() {
//        PropertiesFactoryBean bean = new PropertiesFactoryBean();
//        bean.setLocation(new ClassPathResource("db/libraries-sql.xml"));
//        return bean;
//    }
}
