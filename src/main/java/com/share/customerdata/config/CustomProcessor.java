package com.share.customerdata.config;

import com.share.customerdata.model.Customer;
import org.springframework.batch.item.ItemProcessor;

public class CustomProcessor implements ItemProcessor<Customer, Customer> {

    public Customer process(Customer item) {
        return item;
    }
}
