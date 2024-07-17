package com.nur.service;

import com.nur.dto.CustomerDTO;
import com.nur.dto.MortgageDto;
import com.nur.entity.Mortgage;
import com.nur.exceptions.CustomerNotFoundException;
import com.nur.exceptions.ServiceNotAvailableException;
import com.nur.repository.MortgageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Service
public class MortgageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MortgageService.class);

    @Value("${customer.service.url}")
    private String baseUrl;

    @Autowired
    private MortgageRepository mortgageRepository;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Autowired
    private RestTemplate restTemplate;

    public CustomerDTO getCustomerDetails(Long customerId) {
        String customerServiceUrl = baseUrl + customerId;

        try {
            return webClientBuilder.build()
                    .get()
                    .uri(customerServiceUrl)
                    .retrieve()
                    .onStatus(status -> status.value() == 404, clientResponse -> {
                        LOGGER.error("Customer not found for id: {}", customerId);
                        return Mono.error(new CustomerNotFoundException("Customer not found for id: " + customerId));
                    })
                    .onStatus(status -> status.is5xxServerError(), clientResponse -> {
                        LOGGER.error("Customer Service is down or unreachable at this moment");
                        return Mono.error(new ServiceNotAvailableException("Customer Service is currently unavailable. Please try again later."));
                    })
                    .bodyToMono(CustomerDTO.class)
                    .block();
        } catch (WebClientRequestException e) {
            LOGGER.error("Error fetching customer details: {}", e.getMessage());
            throw new ServiceNotAvailableException("Customer Service is currently unavailable. Please try again later.");
        }
    }


    public Mortgage createMortgage(MortgageDto mortgageDto, Long customerId) {

        CustomerDTO customerResponse = getCustomer(customerId);

//        if (customerResponse.getCivilScore() < 700) {
//            throw new RuntimeException("Customer does not meet the civil score requirement.");
//        }

        Mortgage mortgage = new Mortgage();
        mortgage.setMortgageNumber(mortgageDto.getMortgageNumber());
        mortgage.setAmount(mortgageDto.getAmount());
        mortgage.setInterestRate(mortgageDto.getInterestRate());
        mortgage.setStartDate(mortgageDto.getStartDate());
        mortgage.setEndDate(mortgageDto.getEndDate());
        mortgage.setCustomerId(customerResponse.getId());

        return mortgageRepository.save(mortgage);
    }

    private CustomerDTO getCustomer(Long customerId) {
        String url = baseUrl + customerId;

        try {
            // Create headers and httpEntity for the exchange method
            HttpHeaders headers = new HttpHeaders();
            // Add token or any other necessary headers
            // headers.add("Authorization","token#243"); or //headers.setBearerAuth(token);

            HttpEntity<String> httpEntity = new HttpEntity<>(headers);

            // Make the REST call using exchange method
            ResponseEntity<CustomerDTO> responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, CustomerDTO.class);

            CustomerDTO customerResponse = responseEntity.getBody();
            if (customerResponse == null) {
                throw new CustomerNotFoundException("Customer not found, for id: " + customerId);
            }
            return customerResponse;

        } catch (HttpClientErrorException.NotFound e) {
            LOGGER.error("Customer not found for id: " + customerId, e);
            throw new CustomerNotFoundException("Customer not found for id: " + customerId);
        } catch (HttpServerErrorException | ResourceAccessException e) {
            LOGGER.error("Customer Service is down or unreachable at this moment ", e);
            throw new ServiceNotAvailableException("Customer Service is currently unavailable. Please try again later.");
        } catch (Exception e) {
            LOGGER.error("Error fetching customer details", e);
            throw new ServiceNotAvailableException("Error fetching customer details");
        }
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