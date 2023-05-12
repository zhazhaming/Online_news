package com.example.fles.resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:file-${spring.profiles.active}.properties")
@ConfigurationProperties(prefix = "file")
public class FileResource {
    private String host;

    public String getHost() {
        return host;
    }

    @Value ("${flie.host}")
    public void setHost(String host) {
        this.host = host;
    }
}
