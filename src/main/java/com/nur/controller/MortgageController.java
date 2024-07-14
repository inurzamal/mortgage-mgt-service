package com.nur.controller;

import com.nur.dto.CustomerDTO;
import com.nur.dto.MortgageDto;
import com.nur.entity.Mortgage;
import com.nur.service.MortgageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class MortgageController {

    @Autowired
    private MortgageService mortgageService;

    @PostMapping("/api/mortgage/create/{customerId}")
    public ResponseEntity<Mortgage> createMortgage(@PathVariable Long customerId, @RequestBody MortgageDto mortgageDto) {
        Mortgage mortgage = mortgageService.createMortgage(mortgageDto, customerId);
        return ResponseEntity.ok(mortgage);
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