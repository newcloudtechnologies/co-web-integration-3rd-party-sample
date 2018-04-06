/*
 * Copyright (c) New Cloud Technologies, Ltd., 2013-2018
 *
 * You can not use the contents of the file in any way without New Cloud Technologies Ltd. written permission.
 * To obtain such a permit, you should contact New Cloud Technologies, Ltd. at http://ncloudtech.com/contact.html
 *
 */
package com.ncloudtech.cloudoffice.example.webintegration.controllers;

import com.ncloudtech.cloudoffice.example.webintegration.WebIntegrationProperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class NotificationController {
    @Autowired
    WebIntegrationProperties properties;

    @Autowired
    RestTemplate restTemplate;

    // Re-subscribe each 10 minutes
    @Scheduled(fixedDelayString="PT10M", initialDelay=2000)
    private void subscribe() throws Exception {
        String categories = Arrays.stream(properties.getCoNotifyCategories())
            .map(category -> "category="+category)
            .collect(Collectors.joining("&"));

        String subscribeUrl = properties.getCoNotifyRegisterUrl()
                +"?callbackUrl=" + properties.getCoNotifyCallbackUrl()
                +"&"+categories;

        log.info("Subscribing for events to {}", subscribeUrl);

        restTemplate.postForLocation(subscribeUrl, new HttpEntity<>(""));
    }

    @PostMapping("/notify/me")
    public void callback(@RequestBody String eventData) {
        log.info("CO Notification received: {}", eventData);
    }
}

