package com.kaushik.training.microservice.currencyexchangeservice.persist;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kaushik.training.microservice.currencyexchangeservice.domain.CurrencyExchange;

public interface CurrencyExchangeRepository extends JpaRepository<CurrencyExchange, Long>{

	CurrencyExchange findByFromAndTo(String from, String to); // defined the method signature according to our url, jpa will convert into query.
	
}
