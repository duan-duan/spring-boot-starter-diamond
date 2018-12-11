package com.github.harry.springboot;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.bind.RelaxedDataBinder;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

import java.lang.reflect.Method;
import java.util.Map;


/**
 * @Description:
 * @author: wangruirui
 * @date: 2018/11/22
 */

public class DiamondConfigurer implements BeanFactoryPostProcessor, EnvironmentAware {

    private static final String DIAMOND_PROPERTY_SOURCE_NAME = "DiamondPropertySources";

    private static final Logger logger = LoggerFactory.getLogger(DiamondConfigurer.class);

    private ConfigurableEnvironment environment;

    /**
     * 1、实例化时候执行
     *
     * @param beanFactory
     * @throws BeansException
     */
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        this.fetchPropertiesFromDiamondServer();
    }

    /**
     * 2、从diamond服务器中获取配置信息.<br/>
     * 3、将diamond属性放入到properties里
     */
    protected void fetchPropertiesFromDiamondServer() {

        if (environment.getPropertySources().contains(DIAMOND_PROPERTY_SOURCE_NAME)) {
            // already initialized
            return;
        }

        Diamond diamond = new Diamond();

        String version = SpringBootVersion.getVersion();

        // 2.0 版
        if (version.startsWith("2")) {
            try {
                Class<?> cls = Class.forName("org.springframework.boot.context.properties.bind.Binder");
                Method get = cls.getMethod("get", Environment.class);
                Object binder = get.invoke(null, environment);

                Method bind = cls.getMethod("bind", String.class, Class.class);
                Object result = bind.invoke(binder, "diamond", Diamond.class);

                Method getResult = result.getClass().getMethod("get");
                diamond = (Diamond) getResult.invoke(result);

            } catch (Exception ex) {
                logger.error("spring boot2 binder error", ex);
            }
        } else {
            // 1.x
            try {
                RelaxedPropertyResolver resolver = new RelaxedPropertyResolver(environment);
                Map<String, Object> properties1 = resolver.getSubProperties("");
                // targetClass 目标类型，例如 MapperProperties
                diamond = Diamond.class.newInstance();
                RelaxedDataBinder binder = new RelaxedDataBinder(diamond, "diamond");

                binder.bind(new MutablePropertyValues(properties1));

            } catch (InstantiationException | IllegalAccessException e) {
                logger.error("diamond properties error", e);
                throw new BeanCreationException(e.getMessage());
            }
        }

        CompositePropertySource composite = new CompositePropertySource(DIAMOND_PROPERTY_SOURCE_NAME);

        // 将diamond属性放入到properties里
//        DiamondProperties variables = new DiamondProperties();
//        variables.setDiamondFlags(diamond.getDiamondFlags());
//        variables.init();

        // 注入值
//        composite.addPropertySource(new ConfigPropertySource(DIAMOND_PROPERTY_SOURCE_NAME, variables));

        // 查找第一个applicationConfig的位置
        StringBuilder sb = new StringBuilder();
        environment.getPropertySources().forEach(item -> {
            if (item.getName().startsWith("applicationConfig") && sb.length() == 0) {
                sb.append(item.getName());
            }
        });

        // 加入到本地applicationConfig的前面(优先级低于applicationConfig)
        environment.getPropertySources().addBefore(sb.toString(), composite);
    }

    @Override
    public void setEnvironment(Environment environment) {
        // it is safe enough to cast as all known environment is derived from
        // ConfigurableEnvironment
        this.environment = (ConfigurableEnvironment) environment;
    }
}
