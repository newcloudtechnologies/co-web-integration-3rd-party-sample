/*
 * Copyright (c) New Cloud Technologies, Ltd., 2013-2018
 *
 * You can not use the contents of the file in any way without New Cloud Technologies Ltd. written permission.
 * To obtain such a permit, you should contact New Cloud Technologies, Ltd. at http://ncloudtech.com/contact.html
 *
 */
package com.ncloudtech.cloudoffice.example.webintegration.controllers;

import com.jayway.jsonpath.JsonPath;
import com.ncloudtech.cloudoffice.example.webintegration.WebIntegrationProperties;
import com.ncloudtech.cloudoffice.example.webintegration.exceptions.ApiAccessException;
import com.ncloudtech.cloudoffice.example.webintegration.exceptions.UnauthorizedException;
import com.ncloudtech.cloudoffice.example.webintegration.models.LoginRequest;
import com.ncloudtech.cloudoffice.example.webintegration.models.LoginResponse;
import com.ncloudtech.cloudoffice.example.webintegration.services.MyOfficeClient;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/api")
@Slf4j
public class MainController {

    @Autowired
    private MyOfficeClient myOfficeClient;

    @Autowired
    WebIntegrationProperties properties;


    @ExceptionHandler(UnauthorizedException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    protected ResponseEntity<String> handleAuthException(UnauthorizedException e) {
        return new ResponseEntity<>("User is not authorized\n", HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ApiAccessException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    protected ResponseEntity<String> handleUnknownException(ApiAccessException e) {
        return new ResponseEntity<>(e.getMessage(), e.getStatus());
    }


    @PostMapping("/login")
    private ResponseEntity<LoginResponse> login(@ModelAttribute LoginRequest loginReq) throws Exception {
        log.info("Trying to login {}", loginReq.getLogin());
        Pair<String, HttpHeaders> loginData = myOfficeClient.login(loginReq.getLogin(), loginReq.getPassword());

        String token = JsonPath.compile("$.token").read(loginData.getLeft());
        LoginResponse response = LoginResponse.builder()
                .token(token)
                .coApiUrl(properties.getCoApiUrl())
                .build();

        return new ResponseEntity<>(response, loginData.getRight(), HttpStatus.OK);
    }

}

