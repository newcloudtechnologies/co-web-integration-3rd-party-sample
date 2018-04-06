/*
 * Copyright (c) New Cloud Technologies, Ltd., 2013-2018
 *
 * You can not use the contents of the file in any way without New Cloud Technologies Ltd. written permission.
 * To obtain such a permit, you should contact New Cloud Technologies, Ltd. at http://ncloudtech.com/contact.html
 *
 */
package com.ncloudtech.cloudoffice.example.webintegration.services;

import com.ncloudtech.cloudoffice.example.webintegration.WebIntegrationProperties;
import com.ncloudtech.cloudoffice.example.webintegration.exceptions.UnauthorizedException;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

public class MyOfficeClient {

    @Autowired
    WebIntegrationProperties properties;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    ErrorResponseHandler errorHandler;

    public Pair<String, HttpHeaders> login(String login, String password) throws Exception {
        Map<String, String> loginData = new HashMap<>();
        loginData.put("login", login);
        loginData.put("password", password);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(loginData, headers);

        ResponseEntity<String> response = restTemplate.exchange(properties.getCoAuthUrl()+"/login", HttpMethod.POST, entity, String.class);
        if (response.getStatusCode()!=HttpStatus.OK) {
            throw new UnauthorizedException();
        }
        return new ImmutablePair<>(response.getBody(), response.getHeaders());
    }

}

