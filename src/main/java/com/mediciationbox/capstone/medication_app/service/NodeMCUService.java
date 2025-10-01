package com.mediciationbox.capstone.medication_app.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Service
public class NodeMCUService {

    private static final Logger log = LoggerFactory.getLogger(NodeMCUService.class);
    @Value("${nodemcu.ip.address:192.168.1.11}")
    private String nodeMCUIpAddress;

    private final RestTemplate restTemplate;

    public NodeMCUService(RestTemplateBuilder builder){
        this.restTemplate = builder
                .connectTimeout(Duration.ofSeconds(5))
                .readTimeout(Duration.ofSeconds(70))
                .build();
    }

    public void triggerBuzzer(String medication){
        try{
            String url = "http://" + nodeMCUIpAddress.trim() + "/trigger-buzzer";

            log.info("Attempting to trigger NodeMCU at: {}", url);

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            if(response.getStatusCode().is2xxSuccessful()){
                log.info("Successfully triggered NodeMCU buzzer");
                log.info("NodeMCU response: {}", response.getBody());
            }
        }catch (Exception e){
            log.error("Failed to communicate with NodeMCU at {}: ",
                    "http://" + nodeMCUIpAddress + "/trigger-buzzer", e);
        }
    }
}
