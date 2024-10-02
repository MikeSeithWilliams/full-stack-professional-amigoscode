package com.seith_amigoscode;


import com.seith_amigoscode.customer.Customer;
import com.seith_amigoscode.customer.CustomerRepository;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.Random;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        /*
        Never do this
        CustomerService customerService = new CustomerService(new CustomerDataAccessService());
        CustomerController customerController = new CustomerController(customerService);
        */
        SpringApplication.run(Main.class, args);


    }

    @Bean
    CommandLineRunner runner(CustomerRepository customerRepository) {
        return args -> {
            Faker faker = new Faker();
            Random random = new Random();

            String firstName = faker.name().firstName();
            String lastName = faker.name().lastName();

            Customer customer = new Customer(
                    firstName + " " + lastName,
                    firstName.toLowerCase() + "." + lastName.toLowerCase() + "@example.com",
                    random.nextInt(16,99)
            );


            customerRepository.save(customer);
        };
    }
}
