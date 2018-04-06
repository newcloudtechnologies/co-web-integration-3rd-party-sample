This project is intended to demonstrate the integration of  3rd party web application with the Private Cloud Office (CO) installation.

## Requirements 
1. 3rd party web application must run over the secure connection and on the same base domain as the CO does. 
2. Since the CO version 2018.01 it's allowed to open documents in iframe. In order do it, 3rd party app's domain should be added to CO's configuration:
```
/nct/co/<version>/config/wfe/csp.allowed_frame_ancestors.json: ["https://test-app.base-domain.com"]
```

## This application demonstrates:
1. User's authorization in the CO via the 3-rd party app's backend by the passing through headers from the CO's auth response.

2. Client's subscribtion to the websocket CO notifications for the logged-in user.

3. Backend's subscribtion to the all users's CO notifications [Since CO 2018.01]. (`NotificationController`)

4. File upload with the conversion to the internal format to the root folder of the logged-in user.

5. Opening the uploaded file in the separate tab or in the iframe [Since CO 2018.01].

6. Exporting the document to the PDF format.

## Configuration
The configuration data is located in the src/main/resources/application.properties

```
# CO API base URL
webapp.integration.co-api-url=https://coapi.<base-domain.com>

# CO Auth URL
webapp.integration.co-auth-url=https://auth.<base-domain.com>

# CO Server notification API. Accessible only from the local network by the internal address of CO Auth node
webapp.integration.co-notify-register-url=http://<co-auth-internal-address>:8888/api/v1/notifications/callbacks/register

# 3rd party (our) application callback URL. Must be accessible from the CO system
webapp.integration.co-notify-callback-url=http://<3rd-party-app-address>/notify/me

# Categories to subscribe - comma-separated (all, file, document, user, revision)
webapp.integration.co-notify-categories=file, document

```