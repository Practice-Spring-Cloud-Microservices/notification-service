package com.notificationsservice.service;

import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@FeignClient(name = "api-gateway")
@RibbonClient(name = "mobile-push-notifications")

@RequestMapping("/mobile-push-notifications/mobile-push-notifications/api")
public interface MobilePushNotificationServiceProxy {

    @PostMapping("/endpoint")
    ResponseEntity<Map> registerEndpoint(@RequestBody Map<String, Object> payload);

    @PostMapping("/notification")
    ResponseEntity<Map> pushNotification(@RequestBody Map<String, Object> payload);
}
