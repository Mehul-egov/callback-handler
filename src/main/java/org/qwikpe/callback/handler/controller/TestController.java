package org.qwikpe.callback.handler.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestController.class);

    @GetMapping("/")
    public ResponseEntity<Object> test() {
        LOGGER.info("Welcome to test api of callback handler !!");
        return ResponseEntity.ok("Welcome to test api of callback handler !!");
    }

}
