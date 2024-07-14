package com.nur.service;

import com.nur.dto.CustomerDTO;
import com.nur.dto.MortgageDto;
import com.nur.entity.Mortgage;
import com.nur.repository.MortgageRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
        CustomerDTO customerResponse = restTemplate.getForObject("http://localhost:8080/customer/"+customerId, CustomerDTO.class);
        if (customerResponse == null){
            throw new RuntimeException();
        }
        // Perform additional validation or processing with customerDetails if necessary like checking civil score, etc.

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