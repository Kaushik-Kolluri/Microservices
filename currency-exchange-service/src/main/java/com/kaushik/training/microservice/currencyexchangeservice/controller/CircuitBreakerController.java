package com.kaushik.training.microservice.currencyexchangeservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;

@RestController
public class CircuitBreakerController {

	private Logger logger = LoggerFactory.getLogger(CircuitBreakerController.class);

	@GetMapping("/sample-api")
	@Retry(name = "sample-api", fallbackMethod = "hardcodedResponse")
	public String sampleApi() {

		logger.info("Sample API recieved");

		// making a dummy call intentionally to fail the request.
		ResponseEntity<String> forEntity = new RestTemplate().getForEntity("http://localhost:8080/some-dummy-url",
				String.class);

		return forEntity.getBody();

	}

	@GetMapping("/sample-api-2")
	@CircuitBreaker(name = "default", fallbackMethod = "hardcodedResponse")
	@RateLimiter(name = "default")
	public String sampleApiTwo() {

		logger.info("Sample API 2 recieved");

		// making a dummy call intentionally to fail the request.
		ResponseEntity<String> forEntity = new RestTemplate().getForEntity("http://localhost:8080/some-dummy-url",
				String.class);

		return forEntity.getBody();

	}

	@GetMapping("/sample-api-3")
	@RateLimiter(name = "default")
	public String sampleApiThree() {

		logger.info("Sample API 3 recieved");

		return "sample-api-3";

	}
	
	@GetMapping("/sample-api-4")
	@Bulkhead(name = "default")
	public String sampleApiFour() {

		logger.info("Sample API 3 recieved");

		return "sample-api-4";

	}

	public String hardcodedResponse(Exception ex) {

		return "This is a fallback response";

	}

}
