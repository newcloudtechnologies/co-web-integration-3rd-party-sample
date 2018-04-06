package com.ncloudtech.cloudoffice.example.webintegration.services;

import com.ncloudtech.cloudoffice.example.webintegration.exceptions.ApiAccessException;
import com.ncloudtech.cloudoffice.example.webintegration.exceptions.UnauthorizedException;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ErrorResponseHandler extends DefaultResponseErrorHandler {

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        int statusCode = response.getRawStatusCode();
        try (InputStream content = response.getBody()) {
            throwConcreteException(statusCode, content);
        } finally {
            response.close();
        }
    }

    public void handleError(HttpResponse errorResponse) throws IOException {
        int statusCode = errorResponse.getStatusLine().getStatusCode();
        HttpStatus httpStatus = HttpStatus.valueOf(statusCode);
        HttpEntity errorResponseEntity = errorResponse.getEntity();
        if (errorResponseEntity == null) {
            throw new ApiAccessException(httpStatus, "No error response. Just status "+statusCode);
        } else {
            try (InputStream content = errorResponseEntity.getContent()) {
                throwConcreteException(statusCode, content);
            } finally {
                EntityUtils.consumeQuietly(errorResponseEntity);
            }
        }
    }

    private void throwConcreteException(int statusCode, InputStream content) throws IOException {
        if (statusCode==401 || statusCode==403) {
            throw new UnauthorizedException();
        } else {
            throw new ApiAccessException(HttpStatus.valueOf(statusCode), IOUtils.toString(content, StandardCharsets.UTF_8));
        }
    }

}

