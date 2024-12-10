package com.seith_amigoscode;


import com.seith_amigoscode.customer.Customer;
import com.seith_amigoscode.customer.CustomerRepository;
import com.seith_amigoscode.customer.Gender;
import com.seith_amigoscode.s3.S3Buckets;
import com.seith_amigoscode.s3.S3Service;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

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
    CommandLineRunner runner(
            CustomerRepository customerRepository,
            PasswordEncoder passwordEncoder
//            S3Service s3Service,
//            S3Buckets s3Buckets
    ) {
        return args -> {
            createRandomCustomer(customerRepository, passwordEncoder);
//            testBucketUploadAndDownload(s3Service, s3Buckets);
        };
    }

    private static void testBucketUploadAndDownload(S3Service s3Service, S3Buckets s3Buckets) {
        s3Service.putObject(
                s3Buckets.getCustomer(),
                "foo",
                "Hello World".getBytes()
        );
        byte[] obj = s3Service.getObject(s3Buckets.getCustomer(), "foo");
        System.out.println("Hooray: " + new String(obj));
    }

    private static void createRandomCustomer(CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        Faker faker = new Faker();
        Random random = new Random();

        String firstName = faker.name().firstName();
        String lastName = faker.name().lastName();

        int age = random.nextInt(16, 99);
        Gender gender = age % 2 == 0 ? Gender.MALE : Gender.FEMALE;

        String email = firstName.toLowerCase() + "." + lastName.toLowerCase() + "@example.com";
        Customer customer = new Customer(
                firstName + " " + lastName,
                email,
                passwordEncoder.encode("password"),
                age,
                gender
        );
        customerRepository.save(customer);
        System.out.println("email: " + email);
    }
}
