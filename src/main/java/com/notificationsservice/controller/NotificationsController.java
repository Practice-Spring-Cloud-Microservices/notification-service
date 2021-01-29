package com.notificationsservice.controller;

import com.google.gson.GsonBuilder;
import com.notificationsservice.beans.NotificationBean;
import com.notificationsservice.service.MobilePushNotificationServiceProxy;
import com.notificationsservice.service.NotificationPersistenceServiceProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.lang.System.currentTimeMillis;

@RestController
@RequestMapping("/api")
public class NotificationsController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private Environment environment;
    private DiscoveryClient discoveryClient;
    private NotificationPersistenceServiceProxy notificationPersistenceServiceProxy;
    private MobilePushNotificationServiceProxy mobilePushNotificationServiceProxy;

    @Autowired
    public NotificationsController(Environment environment,
                                   DiscoveryClient discoveryClient,
                                   NotificationPersistenceServiceProxy notificationPersistenceServiceProxy,
                                   MobilePushNotificationServiceProxy mobilePushNotificationServiceProxy) {
        this.environment = environment;
        this.discoveryClient = discoveryClient;
        this.notificationPersistenceServiceProxy = notificationPersistenceServiceProxy;
        this.mobilePushNotificationServiceProxy = mobilePushNotificationServiceProxy;
    }

    @PostMapping("/mobile-endpoint")
    public ResponseEntity<Map> registerMobileEndpoint(@RequestBody Map<String, Object> payload) {
        logger.info("request uri = {}", ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString());
        Map<String, Object> responseMapRaw = new HashMap<>();

        if(isMobilePushNotificationServiceAvailable()) {
            ResponseEntity<Map> registerEndpointResponse = mobilePushNotificationServiceProxy.registerEndpoint(payload);
            responseMapRaw.put("register_mobile_endpoint", responseEntityFormatted(registerEndpointResponse));
        } else {
            responseMapRaw.put("register_mobile_endpoint", HttpStatus.SERVICE_UNAVAILABLE);
        }

        return wrapUpResponse(responseMapRaw);
    }

    @PostMapping("/mobile-notification")
    public ResponseEntity<Map> pushMobileNotificationToUser(@RequestBody Map<String, Object> payload) {
        logger.info("request uri = {}", ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString());
        Map<String, Object> responseMapRaw = new HashMap<>();

        if(isMobilePushNotificationServiceAvailable()) {
            ResponseEntity<Map> pushNotificationResponse = mobilePushNotificationServiceProxy.pushNotification(payload);
            responseMapRaw.put("push_notification", responseEntityFormatted(pushNotificationResponse));

            if(isNotificationPersistenceServiceAvailable()) {
                NotificationBean notificationBean = constructNotificationFromPayload(payload);
                ResponseEntity<Map> persistNotificationResponse = notificationPersistenceServiceProxy.persistNotification(notificationBean);
                responseMapRaw.put("persistNotification", responseEntityFormatted(persistNotificationResponse));
            } else {
                responseMapRaw.put("persistNotification", HttpStatus.SERVICE_UNAVAILABLE);
            }
        } else {
            responseMapRaw.put("push_notification", HttpStatus.SERVICE_UNAVAILABLE);
        }

        return wrapUpResponse(responseMapRaw);
    }

    private boolean isNotificationPersistenceServiceAvailable() {
        return discoveryClient.getInstances("notification-persistence").size() > 0;
    }

    private boolean isMobilePushNotificationServiceAvailable() {
        return discoveryClient.getInstances("mobile-push-notifications").size() > 0;
    }

    private static String toJson(Object obj) {
        return new GsonBuilder()
                .create()
                .toJson(obj);
    }

    private static NotificationBean constructNotificationFromPayload(Map<String, Object> payload) {
        Collection<Integer> userIds = (Collection<Integer>) payload.get("userIds");
        String title = (String) payload.get("title");
        String message = (String) payload.get("message");
        String url = (String) payload.get("url");
        Map<String, String> detailsMap = new HashMap<>();
        detailsMap.put("url", url);

        return new NotificationBean(currentTimeMillis(), "mobile_push", "System", toJson(userIds), null, title, message, toJson(detailsMap));
    }

    private static <T> Map<String, Object> responseEntityFormatted(ResponseEntity<T> responseEntity) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", responseEntity.getStatusCode());
        result.put("body", responseEntity.getBody());
        return result;
    }

    private ResponseEntity<Map> wrapUpResponse(Map<String, Object> responseMapRaw) {
        Map<String, Object> resultMap = new LinkedHashMap<>();

        if(responseMapRaw != null) {
            resultMap.putAll(responseMapRaw);
        }

        HttpStatus status = responseMapRaw != null && !responseMapRaw.isEmpty() ? HttpStatus.OK : HttpStatus.NOT_FOUND;

        return new ResponseEntity<>(resultMap, status);
    }

}
