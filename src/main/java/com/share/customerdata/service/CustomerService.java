package com.share.customerdata.service;

import com.share.customerdata.dao.CustomerRepo;
import com.share.customerdata.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepo customerRepo;

    public String saveCusotmer(Customer cx) {
        customerRepo.save(cx);
        return "Successfully registered customer: " + cx.getId();
    }

    public List<Customer> getAllCustomers() {
        return customerRepo.findAll();
    }
}
