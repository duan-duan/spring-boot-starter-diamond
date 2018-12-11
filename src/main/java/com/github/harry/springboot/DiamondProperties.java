package com.github.harry.springboot;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Description:
 * @author: wangruirui
 * @date: 2018/12/11
 */
@ConfigurationProperties(prefix = "spring.diamond")
public class DiamondProperties {


    private String group;

    private String dataId;

    private Integer order;

    private Boolean ignoreUnresolvablePlaceholders;

    private long timeout = 5000L;

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

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Boolean getIgnoreUnresolvablePlaceholders() {
        return ignoreUnresolvablePlaceholders;
    }

    public void setIgnoreUnresolvablePlaceholders(Boolean ignoreUnresolvablePlaceholders) {
        this.ignoreUnresolvablePlaceholders = ignoreUnresolvablePlaceholders;
    }
}
