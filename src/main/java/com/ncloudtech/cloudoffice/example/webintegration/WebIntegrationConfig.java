/*
 * Copyright (c) New Cloud Technologies, Ltd., 2013-2018
 *
 * You can not use the contents of the file in any way without New Cloud Technologies Ltd. written permission.
 * To obtain such a permit, you should contact New Cloud Technologies, Ltd. at http://ncloudtech.com/contact.html
 *
 */
package com.ncloudtech.cloudoffice.example.webintegration;

import com.ncloudtech.cloudoffice.example.webintegration.controllers.MainController;
import com.ncloudtech.cloudoffice.example.webintegration.controllers.NotificationController;
import com.ncloudtech.cloudoffice.example.webintegration.services.ErrorResponseHandler;
import com.ncloudtech.cloudoffice.example.webintegration.services.MyOfficeClient;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableAutoConfiguration
@EnableScheduling
@PropertySource({
    "classpath:application.properties",
    "file:application.properties",
})
public class WebIntegrationConfig {

    @Bean
    WebIntegrationProperties properties() {
        return new WebIntegrationProperties();
    }

    @Bean
    MainController mainController() {
        return new MainController();
    }

    @Bean
    NotificationController notificeationController() {
        return new NotificationController();
    }

    @Bean
    MyOfficeClient myOfficeClient() {
        return new MyOfficeClient();
    }


    @Bean
    public RestTemplate restTemplate(WebIntegrationProperties properties, ErrorResponseHandler handler) {
        RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setSocketTimeout(properties.getSocketTimeout())
                .setConnectTimeout(properties.getConnectTimeout())
                .setConnectionRequestTimeout(properties.getConnectionTimeout())
                .build();
        HttpClientBuilder httpClientBuilder = HttpClients.custom()
                .setDefaultRequestConfig(defaultRequestConfig);

        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();

        cm.setMaxTotal(properties.getMaxConnections());
        cm.setDefaultMaxPerRoute(properties.getMaxConnections());

        httpClientBuilder.setConnectionManager(cm);

        RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClientBuilder.build()));
        restTemplate.setErrorHandler(handler);
        return restTemplate;
    }

    @Bean
    public ErrorResponseHandler errorResponseHandler() {
        return new ErrorResponseHandler();
    }
}

