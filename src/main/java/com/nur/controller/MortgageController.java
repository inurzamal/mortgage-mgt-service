package com.nur.controller;

import com.nur.dto.CustomerDTO;
import com.nur.dto.MortgageDto;
import com.nur.entity.Mortgage;
import com.nur.service.MortgageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class MortgageController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MortgageController.class);

    @Autowired
    private MortgageService mortgageService;

    @PostMapping("/api/mortgage/create/{customerId}")
    public ResponseEntity<?> createMortgage(@PathVariable Long customerId, @RequestBody MortgageDto mortgageDto) {
        try {
            Mortgage mortgage = mortgageService.createMortgage(mortgageDto, customerId);
            return ResponseEntity.ok(mortgage);
        } catch (RuntimeException e) {
            LOGGER.error("Error creating mortgage: {}", e.getMessage());
            if ( e.getMessage().contains("Customer not found for id")){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(e.getMessage());
        }
    }

    @GetMapping("/api/mortgage/{id}")
    public ResponseEntity<Mortgage> getMortgage(@PathVariable Long id) {
        return mortgageService.getMortgage(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/api/mortgage")
    public ResponseEntity<List<Mortgage>> getAllMortgages() {
        List<Mortgage> mortgages = mortgageService.getAllMortgages();
        return ResponseEntity.ok(mortgages);
    }

    @DeleteMapping("/api/mortgage/delete/{id}")
    public ResponseEntity<Void> deleteMortgage(@PathVariable Long id) {
        mortgageService.deleteMortgage(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/api/mortgage/customer/{customerId}")
    public ResponseEntity<CustomerDTO> getCustomerDetails(@PathVariable Long customerId) {
        CustomerDTO customerDetails = mortgageService.getCustomerDetails(customerId);
        return ResponseEntity.ok(customerDetails);
    }
}