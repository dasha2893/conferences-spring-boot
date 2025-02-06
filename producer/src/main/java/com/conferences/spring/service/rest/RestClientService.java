package com.conferences.spring.service.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Service
public class RestClientService {
    private final RestTemplate restTemplate;

    @Autowired
    public RestClientService(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    public <T> T makeGetRequest(String url, Class<T> responseType, String notFoundMessage) {
        try {
            return restTemplate.getForObject(url, responseType);
        } catch (HttpClientErrorException e) {
            log.warn(notFoundMessage);
            return null;
        } catch (RestClientException e) {
            log.error("Error calling conference-consumer: {}", url, e);
            throw new RuntimeException("Service unavailable", e);
        }
    }

    public <T> List<T> makeGetRequest(String url, ParameterizedTypeReference<List<T>> responseType, String notFoundMessage) {
        try {
            ResponseEntity<List<T>> response = restTemplate.exchange(url, HttpMethod.GET, null, responseType);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            log.warn(notFoundMessage);
            return null;
        } catch (RestClientException e) {
            log.error("Error calling conference-consumer: {}", url, e);
            throw new RuntimeException("Service unavailable", e);
        }
    }

    public <T> T makePostRequest(String url, Object request, Class<T> responseType, String notFoundMessage) {
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Object> entity = new HttpEntity<>(request, httpHeaders);
            ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.POST, entity, responseType);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            log.warn(notFoundMessage);
            return null;
        } catch (RestClientException e) {
            log.error("Error calling conference-consumer: {}", url, e);
            throw new RuntimeException("Service unavailable", e);
        }
    }

    public <T> List<T> makePostRequest(String url, Object request, ParameterizedTypeReference<List<T>> responseType, String notFoundMessage) {
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Object> entity = new HttpEntity<>(request, httpHeaders);
            ResponseEntity<List<T>> response = restTemplate.exchange(url, HttpMethod.POST, entity, responseType);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            log.warn(notFoundMessage);
            return null;
        } catch (RestClientException e) {
            log.error("Error calling conference-consumer: {}", url, e);
            throw new RuntimeException("Service unavailable", e);
        }
    }

    public void makeDeleteRequest(String url, String notFoundMessage) {
        try {
            restTemplate.exchange(url, HttpMethod.DELETE, null, Void.class);
        } catch (HttpClientErrorException e) {
            log.warn(notFoundMessage);
        } catch (RestClientException e) {
            log.error("Error calling conference-consumer: {}", url, e);
            throw new RuntimeException("Service unavailable", e);
        }
    }
}
