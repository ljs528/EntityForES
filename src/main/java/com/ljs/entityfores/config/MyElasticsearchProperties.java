package com.ljs.springbootsearchnotes.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by lijunsai on 2021/05/28
 */
@Data
@Component
@ConfigurationProperties(prefix = "spring.data.myelasticsearch", ignoreUnknownFields = true)
public class MyElasticsearchProperties {
    private Boolean needCheck = false;
    private String esName;
    private String esPassword;
    private String clusterNodes;
    private Boolean entityTransform = false;
    private String entityPath;
}
