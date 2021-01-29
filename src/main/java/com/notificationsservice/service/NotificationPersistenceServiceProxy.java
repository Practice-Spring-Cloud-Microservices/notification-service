package com.notificationsservice.service;

import com.notificationsservice.beans.NotificationBean;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@FeignClient(name = "api-gateway")
@RibbonClient(name = "notification-persistence")

@RequestMapping("/notification-persistence/notification-persistence/api")
public interface NotificationPersistenceServiceProxy {

    @PostMapping("/notifications")
    ResponseEntity<Map> persistNotification(@RequestBody NotificationBean theNotification);

}
