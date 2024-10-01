package com.seith_amigoscode.customer;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository("list")
public class CustomerListDataAccessService implements CustomerDao {

    //database
    private static List<Customer> customers;

    static {
        customers = new ArrayList<>();

        Customer alex = new Customer(
                "Alax",
                "alax@gmail.com",
                21
        );
        customers.add(alex);
        Customer jamila = new Customer(
                "jamila",
                "jamila@gmail.com",
                19
        );
        customers.add(jamila);
    }

    @Override
    public List<Customer> selectAllCustomers() {
        return customers;
    }

    @Override
    public Optional<Customer> selectCustomerById(Long customerId) {
        return customers.stream()
                .filter(c -> c.getId().equals(customerId))
                .findFirst();
    }

    @Override
    public void insertCustomer(Customer customer) {
        customers.add(customer);
    }

    @Override
    public boolean existsCustomerByEmail(String email) {
        return customers.stream().anyMatch(c -> c.getEmail().equals(email));
    }

    @Override
    public boolean existsCustomerById(Long Id) {
        return customers.stream().anyMatch(c -> c.getId().equals(Id));
    }

    @Override
    public void deleteCustomerById(Long customerId) {
        customers.removeIf(c -> c.getId().equals(customerId));
    }

    @Override
    public void updateCustomer(Customer customer) {
        customers.add(customer);
    }
}
