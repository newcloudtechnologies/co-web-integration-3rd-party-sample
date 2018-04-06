/*
 * Copyright (c) New Cloud Technologies, Ltd., 2013-2018
 *
 * You can not use the contents of the file in any way without New Cloud Technologies Ltd. written permission.
 * To obtain such a permit, you should contact New Cloud Technologies, Ltd. at http://ncloudtech.com/contact.html
 *
 */
package com.ncloudtech.cloudoffice.example.webintegration.models;

import lombok.Data;

@Data
public class LoginRequest {
    String login;
    String password;
}

