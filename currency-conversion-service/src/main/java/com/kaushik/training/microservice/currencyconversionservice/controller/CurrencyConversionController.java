package com.kaushik.training.microservice.currencyconversionservice.controller;

import java.math.BigDecimal;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.kaushik.training.microservice.currencyconversionservice.domain.CurrencyConversion;
import com.kaushik.training.microservice.currencyconversionservice.proxy.CurrencyExchangeProxy;

//created this class to enable request tracing for rest template method call. If we use new RestTemplate directly, its not possible to trace. Instead we autowire the bean from this class.
@Configuration(proxyBeanMethods = false) 
class RestTemplateConfiguration {
    
    @Bean
    RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
}

@RestController
public class CurrencyConversionController {
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private CurrencyExchangeProxy proxy;
	
	// method using rest template to communicate with currency exchange service.
	@GetMapping("/currency-conversion/from/{from}/to/{to}/quantity/{quantity}")
	public CurrencyConversion calculateCurrencyConversion(
			@PathVariable String from,
			@PathVariable String to,
			@PathVariable BigDecimal quantity) {
	
		HashMap<String, String> uriVariables = new HashMap<>();
		
		uriVariables.put("from", from);
		uriVariables.put("to", to);
		
		ResponseEntity<CurrencyConversion> responseEntity = restTemplate.getForEntity("http://localhost:8000/currency-exchange/from/{from}/to/{to}", 
							CurrencyConversion.class, uriVariables);
		
		CurrencyConversion currencyConversion = responseEntity.getBody(); // has the response of currency exchange service.
		
		// returns the calculated conversion value with the help of above conversion multiple value from the currency exchange service.
		return new CurrencyConversion(currencyConversion.getId(), from, to,
				 	currencyConversion.getConversionMultiple(), quantity, 
				 	quantity.multiply(currencyConversion.getConversionMultiple()), 
				 	currencyConversion.getEnvironment() + " rest template");
		
	}
	
	// method using feign to communicate with currency exchange service.
	@GetMapping("/currency-conversion-feign/from/{from}/to/{to}/quantity/{quantity}")
	public CurrencyConversion calculateCurrencyConversionFeign(
			@PathVariable String from,
			@PathVariable String to,
			@PathVariable BigDecimal quantity) {
	
		CurrencyConversion currencyConversion = proxy.retrieveExchangeValue(from, to);
		
		// returns the calculated conversion value with the help of above conversion multiple value from the currency exchange service.
		return new CurrencyConversion(currencyConversion.getId(), from, to,
				 	currencyConversion.getConversionMultiple(), quantity, 
				 	quantity.multiply(currencyConversion.getConversionMultiple()), 
				 	currencyConversion.getEnvironment() + " feign");
		
	}

}
