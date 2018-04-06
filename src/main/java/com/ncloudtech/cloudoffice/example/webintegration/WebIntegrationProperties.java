/*
 * Copyright (c) New Cloud Technologies, Ltd., 2013-2018
 *
 * You can not use the contents of the file in any way without New Cloud Technologies Ltd. written permission.
 * To obtain such a permit, you should contact New Cloud Technologies, Ltd. at http://ncloudtech.com/contact.html
 *
 */

package com.ncloudtech.cloudoffice.example.webintegration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * @author alxeg
 * @since 2018-04-03
 */
@ConfigurationProperties("webapp.integration")
@Data
public class WebIntegrationProperties {
    // CO API base URL
    private String coApiUrl;
    // CO Auth URL
    private String coAuthUrl;

    //CO Server notification API. Accessible only from the local network by the internal address of CO Auth node.
    private String coNotifyRegisterUrl;
    // 3rd party (our) application callback URL. Must be accessible from the CO system.
    private String coNotifyCallbackUrl;
    // Categories to subscribe - comma-separated (all, file, document, user, revision)
    private String[] coNotifyCategories;

    private int socketTimeout=-1;
    private int connectTimeout=15000;
    private int connectionTimeout=1500;
    private int maxConnections=100;
}

