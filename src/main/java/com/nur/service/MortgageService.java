package com.nur.service;

import com.nur.dto.CustomerDTO;
import com.nur.dto.MortgageDto;
import com.nur.entity.Mortgage;
import com.nur.repository.MortgageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Optional;

@Service
public class MortgageService {

    @Autowired
    private MortgageRepository mortgageRepository;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Autowired
    private RestTemplate restTemplate;

    public CustomerDTO getCustomerDetails(Long customerId) {
        String customerServiceUrl = "http://localhost:8080/customer/" + customerId;
        CustomerDTO customerDetails = webClientBuilder.build()
                .get()
                .uri(customerServiceUrl)
                .retrieve()
                .bodyToMono(CustomerDTO.class)
                .block();
        return customerDetails;
    }

    public Mortgage createMortgage(MortgageDto mortgageDto, Long customerId) {

        String url = "http://localhost:8080/customer/" + customerId;

        // Create headers and httpEntity for the exchange method
        HttpHeaders headers = new HttpHeaders(); // headers.add("Authorization","token#243"); or //headers.setBearerAuth(token);


        HttpEntity<String> httpEntity = new HttpEntity<>(headers);

        // Make the REST call using exchange method
        ResponseEntity<CustomerDTO> responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, CustomerDTO.class);

        // Get the response body
        CustomerDTO customerResponse = responseEntity.getBody();

        if (customerResponse == null) {
            throw new RuntimeException("Customer not found for id: " + customerId);
        }

//        if (customerResponse.getCivilScore() < 700) {
//            throw new RuntimeException("Customer does not meet the civil score requirement.");
//        }

        // Create and save the mortgage
        Mortgage mortgage = new Mortgage();
        mortgage.setMortgageNumber(mortgageDto.getMortgageNumber());
        mortgage.setAmount(mortgageDto.getAmount());
        mortgage.setInterestRate(mortgageDto.getInterestRate());
        mortgage.setStartDate(mortgageDto.getStartDate());
        mortgage.setEndDate(mortgageDto.getEndDate());
        mortgage.setCustomerId(customerId);

        return mortgageRepository.save(mortgage);
    }

    public Optional<Mortgage> getMortgage(Long id) {
        return mortgageRepository.findById(id);
    }

    public List<Mortgage> getAllMortgages() {
        return mortgageRepository.findAll();
    }

    public void deleteMortgage(Long id) {
        mortgageRepository.deleteById(id);
    }
}