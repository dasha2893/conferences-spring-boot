package com.conferences.spring.service.rest;

import com.conferences.spring.config.jwt.JwtTokenStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class RestClientService {
    private final RestTemplate restTemplate;
    private final JwtTokenStorage jwtTokenStorage;

    @Autowired
    public RestClientService(RestTemplate restTemplate,
                             JwtTokenStorage jwtTokenStorage){
        this.restTemplate = restTemplate;
        this.jwtTokenStorage = jwtTokenStorage;
    }

    public <T> T makeGetRequest(String url, Class<T> responseType, String notFoundMessage, String email) {
        try {
            HttpHeaders httpHeaders = getHeaders(email);
            HttpEntity<Object> entity = new HttpEntity<>(null, httpHeaders);

            ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.GET, entity, responseType);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            log.warn(notFoundMessage);
            return null;
        } catch (RestClientException e) {
            log.error("Error calling conference-consumer: {}", url, e);
            throw new RuntimeException("Service unavailable", e);
        }
    }

    public <T> List<T> makeGetRequest(String url, ParameterizedTypeReference<List<T>> responseType, String notFoundMessage, String email) {
        try {
            HttpHeaders httpHeaders = getHeaders(email);
            HttpEntity<Object> entity = new HttpEntity<>(null, httpHeaders);

            ResponseEntity<List<T>> response = restTemplate.exchange(url, HttpMethod.GET, entity, responseType);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            log.warn(notFoundMessage);
            return null;
        } catch (RestClientException e) {
            log.error("Error calling conference-consumer: {}", url, e);
            throw new RuntimeException("Service unavailable", e);
        }
    }

    public <T> T makePostRequest(String url, Object request, Class<T> responseType, String notFoundMessage, String email) {
        try {
            HttpHeaders httpHeaders = getHeaders(email);
            HttpEntity<Object> entity = new HttpEntity<>(request, httpHeaders);

            ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.POST, entity, responseType);
            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            log.warn(notFoundMessage);
            return null;
        } catch (RestClientException e) {
            log.error("Error calling conference-consumer: {}", url, e);
            throw new RuntimeException("Service unavailable", e);
        }
    }

    public <T, K> Map<T, K> makePostRequest(String url, Object request, ParameterizedTypeReference<Map<T, K>> responseType, String notFoundMessage, String email) {
        try {
            HttpHeaders httpHeaders = getHeaders(email);
            HttpEntity<Object> entity = new HttpEntity<>(request, httpHeaders);

            ResponseEntity<Map<T, K>> response = restTemplate.exchange(url, HttpMethod.POST, entity, responseType);
            log.info("makePostRequest headers: {}", response.getHeaders().toString());
            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            log.warn(notFoundMessage);
            return null;
        } catch (RestClientException e) {
            log.error("Error calling conference-consumer: {}", url, e);
            throw new RuntimeException("Service unavailable", e);
        }
    }

    public void makeDeleteRequest(String url, String notFoundMessage, String email) {
        try {
            HttpHeaders httpHeaders = getHeaders(email);
            HttpEntity<Object> entity = new HttpEntity<>(null, httpHeaders);

            restTemplate.exchange(url, HttpMethod.DELETE, entity, Void.class);
        } catch (HttpClientErrorException.NotFound e) {
            log.warn(notFoundMessage);
        } catch (RestClientException e) {
            log.error("Error calling conference-consumer: {}", url, e);
            throw new RuntimeException("Service unavailable", e);
        }
    }

    private HttpHeaders getHeaders(String email) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.add("Authorization", "Bearer " + jwtTokenStorage.getToken(email));
        return httpHeaders;
    }
}
