package org.github.mybatisextend.mybatis.autoconfigura;


import com.asiainfo.springcloud.mybatis.multitydatasource.datasource.MultityDataSourceConf;
import com.asiainfo.springcloud.mybatis.multitydatasource.session.MultityDataSourceSessionTemplate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.mybatis.spring.mapper.ClassPathMapperScanner;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.Iterator;
import java.util.List;


/**
 * @author zhouyh3 on 2019/3/6.
 */
@Configuration
@EnableConfigurationProperties({MybatisProperties.class,MultityDataSourceConf.class})
@ConditionalOnClass({SqlSessionFactory.class, SqlSessionFactoryBean.class})
@ConditionalOnBean({DataSource.class})
@AutoConfigureAfter({DataSourceAutoConfiguration.class})
public class MultitybatisConfiguration implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(MultitybatisConfiguration.class);

    private final MybatisProperties properties;
    private final Interceptor[] interceptors;
    private final ResourceLoader resourceLoader;
    private final DatabaseIdProvider databaseIdProvider;
    private final List<ConfigurationCustomizer> configurationCustomizers;
    public static org.apache.ibatis.session.Configuration configuration;

    public MultitybatisConfiguration(MybatisProperties properties, ObjectProvider<Interceptor[]> interceptorsProvider, ResourceLoader resourceLoader, ObjectProvider<DatabaseIdProvider> databaseIdProvider, ObjectProvider<List<ConfigurationCustomizer>> configurationCustomizersProvider) {
        this.properties = properties;
        this.interceptors = (Interceptor[])interceptorsProvider.getIfAvailable();
        this.resourceLoader = resourceLoader;
        this.databaseIdProvider = (DatabaseIdProvider)databaseIdProvider.getIfAvailable();
        this.configurationCustomizers = (List)configurationCustomizersProvider.getIfAvailable();
    }

    @Override
    public void afterPropertiesSet() {
        this.checkConfigFileExists();
    }

    private void checkConfigFileExists() {
        if(this.properties.isCheckConfigLocation() && StringUtils.hasText(this.properties.getConfigLocation())) {
            Resource resource = this.resourceLoader.getResource(this.properties.getConfigLocation());
            Assert.state(resource.exists(), "Cannot find config location: " + resource + " (please add config file or check your Mybatis configuration)");
        }

    }

    @Configuration
    @Import({MybatisAutoConfiguration.AutoConfiguredMapperScannerRegistrar.class})
    @ConditionalOnMissingBean({MapperFactoryBean.class})
    public static class MapperScannerRegistrarNotFoundConfiguration implements InitializingBean {
        public MapperScannerRegistrarNotFoundConfiguration() {
        }

        @Override
        public void afterPropertiesSet() {
            MultitybatisConfiguration.logger.debug("No {} found.", MapperFactoryBean.class.getName());
        }
    }

    public static class AutoConfiguredMapperScannerRegistrar implements BeanFactoryAware, ImportBeanDefinitionRegistrar, ResourceLoaderAware {
        private BeanFactory beanFactory;
        private ResourceLoader resourceLoader;

        public AutoConfiguredMapperScannerRegistrar() {
        }

        @Override
        public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
            if(!AutoConfigurationPackages.has(this.beanFactory)) {
                MultitybatisConfiguration.logger.debug("Could not determine auto-configuration package, automatic mapper scanning disabled.");
            } else {
                MultitybatisConfiguration.logger.debug("Searching for mappers annotated with @Mapper");
                List packages = AutoConfigurationPackages.get(this.beanFactory);
                if(MultitybatisConfiguration.logger.isDebugEnabled()) {
                    Iterator scanner = packages.iterator();

                    while(scanner.hasNext()) {
                        String pkg = (String)scanner.next();
                        MultitybatisConfiguration.logger.debug("Using auto-configuration base package \'{}\'", pkg);
                    }
                }

                ClassPathMapperScanner scanner1 = new ClassPathMapperScanner(registry);
                if(this.resourceLoader != null) {
                    scanner1.setResourceLoader(this.resourceLoader);
                }

                scanner1.setAnnotationClass(Mapper.class);
                scanner1.registerFilters();
                scanner1.doScan(StringUtils.toStringArray(packages));
            }
        }

        @Override
        public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
            this.beanFactory = beanFactory;
        }

        @Override
        public void setResourceLoader(ResourceLoader resourceLoader) {
            this.resourceLoader = resourceLoader;
        }
    }

    @Scope("prototype")
    @Bean
    public static com.asiainfo.springcloud.mybatis.multitydatasource.Configuration getMultityConfigurationInvocationHandler(){
        return new com.asiainfo.springcloud.mybatis.multitydatasource.Configuration(configuration);
    }



    //@Bean
//    public  org.apache.ibatis.session.Configuration getMybatisConfiguration(InterceptorConf interceptorConf)throws Exception{
//        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
//        // 使全局的映射器启用或禁用缓存。
//        configuration.setCacheEnabled(true);
//        // 全局启用或禁用延迟加载。当禁用时，所有关联对象都会即时加载。
//        configuration.setLazyLoadingEnabled(true);
//        // 当启用时，有延迟加载属性的对象在被调用时将会完全加载任意属性。否则，每种属性将会按需要加载。
//        configuration.setAggressiveLazyLoading(true);
//        // 是否允许单条sql 返回多个数据集  (取决于驱动的兼容性) default:true
//        configuration.setMultipleResultSetsEnabled(true);
//        // 是否可以使用列的别名 (取决于驱动的兼容性) default:true
//        configuration.setUseColumnLabel(true);
//        // 允许JDBC 生成主键。需要驱动器支持。如果设为了true，这个设置将强制使用被生成的主键，有一些驱动器不兼容不过仍然可以执行。  default:false
//        configuration.setUseGeneratedKeys(false);
//        // 指定 MyBatis 如何自动映射 数据基表的列 NONE：不隐射　PARTIAL:部分  FULL:全部
//        configuration.setAutoMappingBehavior(AutoMappingBehavior.PARTIAL);
//        // 这是默认的执行类型  （SIMPLE: 简单； REUSE: 执行器可能重复使用prepared statements语句；BATCH: 执行器可以重复执行语句和批量更新）
//        configuration.setDefaultExecutorType(ExecutorType.SIMPLE);
//        // 使用驼峰命名法转换字段。
//        configuration.setMapUnderscoreToCamelCase(true);
//        // 设置本地缓存范围 session:就会有数据的共享  statement:语句范围 (这样就不会有数据的共享 ) defalut:session
//        configuration.setLocalCacheScope(LocalCacheScope.SESSION);
//        // 设置但JDBC类型为空时,某些驱动程序 要指定值,default:OTHER，插入空值时不需要指定类型
//        configuration.setJdbcTypeForNull(JdbcType.NULL);
//
//        List<String> interceptorClassNames = interceptorConf.getClassNameList();
//        if(null != interceptorClassNames){
//            for(String interceptorClassName : interceptorClassNames){
//                Interceptor interceptor  = (Interceptor)Class.forName(interceptorClassName).newInstance();
//                configuration.addInterceptor(interceptor);
//            }
//        }
//
//        return configuration;
//    }

    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        factory.setVfs(SpringBootVFS.class);
        if(StringUtils.hasText(this.properties.getConfigLocation())) {
            factory.setConfigLocation(this.resourceLoader.getResource(this.properties.getConfigLocation()));
        }

        org.apache.ibatis.session.Configuration configuration = this.properties.getConfiguration();
        if(configuration == null && !StringUtils.hasText(this.properties.getConfigLocation())) {
            configuration = new org.apache.ibatis.session.Configuration();
        }

        if(configuration != null && !CollectionUtils.isEmpty(this.configurationCustomizers)) {
            Iterator var4 = this.configurationCustomizers.iterator();

            while(var4.hasNext()) {
                ConfigurationCustomizer customizer = (ConfigurationCustomizer)var4.next();
                customizer.customize(configuration);
            }
        }

        factory.setConfiguration(configuration);
        if(this.properties.getConfigurationProperties() != null) {
            factory.setConfigurationProperties(this.properties.getConfigurationProperties());
        }

        if(!ObjectUtils.isEmpty(this.interceptors)) {
            factory.setPlugins(this.interceptors);
        }

        if(this.databaseIdProvider != null) {
            factory.setDatabaseIdProvider(this.databaseIdProvider);
        }

        if(StringUtils.hasLength(this.properties.getTypeAliasesPackage())) {
            factory.setTypeAliasesPackage(this.properties.getTypeAliasesPackage());
        }

        if(this.properties.getTypeAliasesSuperType() != null) {
            factory.setTypeAliasesSuperType(this.properties.getTypeAliasesSuperType());
        }

        if(StringUtils.hasLength(this.properties.getTypeHandlersPackage())) {
            factory.setTypeHandlersPackage(this.properties.getTypeHandlersPackage());
        }

        if(!ObjectUtils.isEmpty(this.properties.resolveMapperLocations())) {
            factory.setMapperLocations(this.properties.resolveMapperLocations());
        }

        MultitybatisConfiguration.configuration = configuration;
        return factory.getObject();
    }

//    @Primary
//    @Bean
//    public SqlSessionFactory getDataSourceSessionFactory(MultityDataSourceConf mybatisConf,com.asiainfo.springcloud.mybatis.multitydatasource.Configuration configuration)throws Exception{
//        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
//        sqlSessionFactoryBean.setDataSource(mybatisConf.getDefaultDatasource());
//        sqlSessionFactoryBean.setConfiguration(configuration);
//
//
//        return sqlSessionFactoryBean.getObject();
//    }


    @Bean
    @Primary
    public SqlSessionTemplate getMultityDatasourceSqlSessionTemplate(MultityDataSourceConf mybatisConf,SqlSessionFactory defaultSessionFactory)throws Exception{
        MultityDataSourceSessionTemplate multityDataSourceSessionTemplate = new MultityDataSourceSessionTemplate(defaultSessionFactory);
        multityDataSourceSessionTemplate.setDataSourceConf(mybatisConf);
        return multityDataSourceSessionTemplate;
    }

}
