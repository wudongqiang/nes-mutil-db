package com.nes.mutil.nesmutildb.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 动态创建多数据源注册到Spring中
 */
//@Configuration
public class MultipleDataSourceBeanDefinitionRegistryPostProcessor
        implements BeanDefinitionRegistryPostProcessor, EnvironmentAware {

    private static final Logger logger = LoggerFactory
            .getLogger(MultipleDataSourceBeanDefinitionRegistryPostProcessor.class);

    // 如配置文件中未指定数据源类型，使用该默认值
//    private static final Object DATASOURCE_TYPE_DEFAULT = "org.apache.tomcat.jdbc.pool.DataSource";
  private static final Object DATASOURCE_TYPE_DEFAULT = "com.zaxxer.hikari.HikariDataSource";

    private ScopeMetadataResolver scopeMetadataResolver = new AnnotationScopeMetadataResolver();
    private BeanNameGenerator beanNameGenerator = new AnnotationBeanNameGenerator();

    // 存放DataSource配置的集合，模型<dataSourceName,dataSourceMap>
    private Map<String, Map<String, Object>> dataSourceMap = new HashMap<>();

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        logger.info("Invoke Metho postProcessBeanFactory");
        beanFactory.getBeanDefinition("dataSource").setPrimary(true);

        BeanDefinition bd = null;
        Map<String, Object> dsMap = null;
        for (Entry<String, Map<String, Object>> entry : dataSourceMap.entrySet()) {
            bd = beanFactory.getBeanDefinition(entry.getKey());
            MutablePropertyValues mpv = bd.getPropertyValues();
            dsMap = entry.getValue();
            mpv.addPropertyValue("driver-class-name", dsMap.get("driver-class-name"));
            mpv.addPropertyValue("url", dsMap.get("url"));
            mpv.addPropertyValue("username", dsMap.get("username"));
            mpv.addPropertyValue("password", dsMap.get("password"));
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        logger.info("Invoke Metho postProcessBeanDefinitionRegistry");

        try {
            if (!dataSourceMap.isEmpty()) {
                for (Entry<String, Map<String, Object>> entry : dataSourceMap.entrySet()) {

                    Object type = entry.getValue().get("type");
                    if (type == null)
                        type = DATASOURCE_TYPE_DEFAULT;// 默认DataSource
                    registerBean(registry, entry.getKey(), (Class<? extends DataSource>) Class.forName(type.toString()));
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    /**
     * 注册Bean到Spring
     *
     * @param registry
     * @param name
     * @param beanClass
     */
    private void registerBean(BeanDefinitionRegistry registry, String name, Class<?> beanClass) {
        AnnotatedGenericBeanDefinition abd = new AnnotatedGenericBeanDefinition(beanClass);

        ScopeMetadata scopeMetadata = this.scopeMetadataResolver.resolveScopeMetadata(abd);
        abd.setScope(scopeMetadata.getScopeName());
        // 可以自动生成name
        String beanName = (name != null ? name : this.beanNameGenerator.generateBeanName(abd, registry));

        AnnotationConfigUtils.processCommonDefinitionAnnotations(abd);

        BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(abd, beanName);
        BeanDefinitionReaderUtils.registerBeanDefinition(definitionHolder, registry);
    }

    /**
     * 加载多数据源配置
     */
    @Override
    public void setEnvironment(Environment env) {
//        RelaxedPropertyResolver propertyResolver = new RelaxedPropertyResolver(env, "custom.datasource.");
//        String dsPrefixs = propertyResolver.getProperty("names");
//        for (String dsPrefix : dsPrefixs.split(",")) {// 多个数据源
//            Map<String, Object> dsMap = propertyResolver.getSubProperties(dsPrefix + ".");
//            dataSourceMap.put(dsPrefix, dsMap);
//        }
        Binder binder = Binder.get(env);
        BindResult<Map> bind = binder.bind("custom.datasource", Map.class);
        Map<String,Object> map = bind.get();

        String dsPrefixs = (String)map.get("names");
        for (String dsPrefix : dsPrefixs.split(",")) {// 多个数据源
            Map<String, Object> dsMap = (Map<String,Object>)map.get(dsPrefix);
            dataSourceMap.put(dsPrefix, dsMap);
        }
    }


}