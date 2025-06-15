package com.damian.whatsapp.customer;

import com.damian.whatsapp.customer.dto.CustomerDTO;
import com.damian.whatsapp.customer.dto.CustomerDTOMapper;
import com.damian.whatsapp.customer.dto.CustomerWithProfileDTO;
import com.damian.whatsapp.customer.http.request.CustomerEmailUpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1")
@RestController
public class CustomerController {
    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    // endpoint to receive logged customer
    @GetMapping("/customers/me")
    public ResponseEntity<?> getLoggedCustomerData() {
        Customer customer = customerService.getCustomer();
        CustomerWithProfileDTO dto = CustomerDTOMapper.toCustomerWithProfileDTO(customer);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(dto);
    }

    // endpoint to modify logged customer email
    @PatchMapping("/customers/me/email")
    public ResponseEntity<?> updateLoggedCustomerEmail(
            @Validated @RequestBody
            CustomerEmailUpdateRequest request
    ) {
        Customer customer = customerService.updateEmail(request);
        CustomerDTO customerDTO = CustomerDTOMapper.toCustomerDTO(customer);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(customerDTO);
    }
}

