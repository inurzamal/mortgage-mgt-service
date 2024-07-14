package com.nur.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;


@Service
public class CustomerServiceClient {


    private RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private WebClient.Builder webClientBuilder;

    public String getCustomerDetails(Long customerId) {
        String customerServiceUrl = "http://customer-service/api/customers/" + customerId;
        String customerDetails = restTemplate.getForObject(customerServiceUrl, String.class);
        return customerDetails;
    }

    public String getCustomerDetailsUsingWebClient(Long customerId) {
        String customerServiceUrl = "http://customer-service/api/customers/" + customerId;
        String customerDetails = webClientBuilder.build()
                .get()
                .uri(customerServiceUrl)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        return customerDetails;
    }
}