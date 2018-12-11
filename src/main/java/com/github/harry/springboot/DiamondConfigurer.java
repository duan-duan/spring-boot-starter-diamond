package com.github.harry.springboot;


import com.taobao.diamond.manager.DiamondManager;
import com.taobao.diamond.manager.ManagerListener;
import com.taobao.diamond.manager.impl.DefaultDiamondManager;
import org.apache.commons.io.IOUtils;
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;


/**
 * @Description:
 * @author: wangruirui
 * @date: 2018/11/22
 */

public class DiamondConfigurer implements BeanFactoryPostProcessor, EnvironmentAware , ManagerListener {

    private static final String DIAMOND_PROPERTY_SOURCE_NAME = "DiamondPropertySources";

    private static final Logger logger = LoggerFactory.getLogger(DiamondConfigurer.class);

    private ConfigurableEnvironment environment;

    private String group;
    private String dataId;
    private long timeout = 5000L;
    private DiamondManager diamondManager;
    private final Properties properties = new Properties();

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

        DiamondProperties diamond = new DiamondProperties();

        String version = SpringBootVersion.getVersion();

        // 2.0 版
        if (version.startsWith("2")) {
            try {
                Class<?> cls = Class.forName("org.springframework.boot.context.properties.bind.Binder");
                Method get = cls.getMethod("get", Environment.class);
                Object binder = get.invoke(null, environment);

                Method bind = cls.getMethod("bind", String.class, Class.class);
                Object result = bind.invoke(binder, "diamond", DiamondProperties.class);

                Method getResult = result.getClass().getMethod("get");
                diamond = (DiamondProperties) getResult.invoke(result);

            } catch (Exception ex) {
                logger.error("spring boot2 binder error", ex);
            }
        } else {
            // 1.x
            try {
                RelaxedPropertyResolver resolver = new RelaxedPropertyResolver(environment);
                Map<String, Object> properties1 = resolver.getSubProperties("");
                // targetClass 目标类型，例如 MapperProperties
                diamond = DiamondProperties.class.newInstance();
                RelaxedDataBinder binder = new RelaxedDataBinder(diamond, "diamond");

                binder.bind(new MutablePropertyValues(properties1));

            } catch (InstantiationException | IllegalAccessException e) {
                logger.error("diamond properties error", e);
                throw new BeanCreationException(e.getMessage());
            }
        }

        CompositePropertySource composite = new CompositePropertySource(DIAMOND_PROPERTY_SOURCE_NAME);

        // 将diamond属性放入到properties里
        this.setGroup(diamond.getGroup());
        this.setDataId(diamond.getDataId());
        this.diamondManager = new DefaultDiamondManager(group, dataId, this);
        String availableConfInfo = this.diamondManager.getAvailableConfigureInfomation(this.timeout);

        logger.info("diamond配置信息初始化.");
        this.processRecvConfigInfo(availableConfInfo);



        // 注入值
        composite.addPropertySource(new ConfigPropertySource(DIAMOND_PROPERTY_SOURCE_NAME, properties));

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


    /**
     *  3、将diamond属性放入到properties里
     * @param availableConfInfo
     */
    private void processRecvConfigInfo(String availableConfInfo) {
        InputStream input = null;
        try {
            Properties variables = new Properties();
            input = new ByteArrayInputStream(availableConfInfo.getBytes("UTF-8"));
            variables.load(input);
            this.properties.clear();
            this.properties.putAll(variables);
//            this.setProperties(this.properties);
        } catch (UnsupportedEncodingException ex) {
            logger.error("获取diamond配置信息出错", ex);
            throw new RuntimeException(ex);
        } catch (IOException ex) {
            logger.error("获取diamond配置信息出错", ex);
            throw new RuntimeException(ex);
        } catch (RuntimeException ex) {
            logger.error("获取diamond配置信息出错", ex);
            throw ex;
        } finally {
            IOUtils.closeQuietly(input);
        }
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    @Override
    public Executor getExecutor() {
        return null;
    }

    @Override
    public void receiveConfigInfo(String configInfo) {
        logger.info("diamond配置信息发生变更.");
        try {
            this.processRecvConfigInfo(configInfo);
        } catch (RuntimeException ex) {
            logger.error("接收配置处理失败!", ex);
        }

    }








}
