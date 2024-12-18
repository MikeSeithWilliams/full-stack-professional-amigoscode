package com.seith_amigoscode.customer;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerDao {
    List<Customer> selectAllCustomers();
    Optional<Customer> selectCustomerById(Long customerId);
    void insertCustomer(Customer customer);
    boolean existsCustomerByEmail(String email);
    boolean existsCustomerById(Long customerId);
    void deleteCustomerById(Long customerId);
    void updateCustomer(Customer customer);
    Optional<Customer> selectUserByEmail(String email);
    void updateCustomerProfileId(String profileImageId, Long customerId);
}
