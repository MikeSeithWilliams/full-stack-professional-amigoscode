package com.seith_amigoscode.customer;

import com.seith_amigoscode.exception.DuplicateResourceException;
import com.seith_amigoscode.exception.RequestValidationException;
import com.seith_amigoscode.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private final CustomerDao customerDao;
    private final PasswordEncoder passwordEncoder;
    private final CustomerDTOMapper customerDTOMapper;

    public CustomerService(@Qualifier("jdbc") CustomerDao customerDao, PasswordEncoder passwordEncoder, CustomerDTOMapper customerDTOMapper) {
        this.customerDao = customerDao;
        this.passwordEncoder = passwordEncoder;
        this.customerDTOMapper = customerDTOMapper;
    }

    public List<CustomerDTO> getAllCustomers(){
        return customerDao.selectAllCustomers()
                .stream()
                .map(customerDTOMapper)
                .collect(Collectors.toList());
    }

    public CustomerDTO getCustomer(Long id){
        return customerDao.selectCustomerById(id)
                .map(customerDTOMapper)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Customer with id [%s] not found".formatted(id))
                );
    }

    public void addCustomer(CustomerRegistrationRequest customerRegistrationRequest){
        // check if email exists
        String email = customerRegistrationRequest.email();
        if (customerDao.existsCustomerByEmail(email)){
            throw new DuplicateResourceException("email already taken");
        }
        // add
        Customer customer = new Customer(
                customerRegistrationRequest.name(),
                customerRegistrationRequest.email(),
                passwordEncoder.encode(customerRegistrationRequest.password()),
                customerRegistrationRequest.age(),
                customerRegistrationRequest.gender()
        );
        customerDao.insertCustomer(customer);
    }

    public void deleteCustomerById(Long id){
        if (!customerDao.existsCustomerById(id)){
            throw new ResourceNotFoundException("Customer with id [%s] not found".formatted(id));
        }
        customerDao.deleteCustomerById(id);
    }

    public void updateCustomer(Long id, CustomerUpdateRequest updateRequest){
        Customer customer = customerDao.selectCustomerById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Customer with id [%s] not found".formatted(id))
                );

        boolean changes = false;

        if (updateRequest.name() != null && !updateRequest.name().equals(customer.getName())){
            customer.setName(updateRequest.name());
            changes = true;
        }

        if (updateRequest.age() != null && !updateRequest.age().equals(customer.getAge())){
            customer.setAge(updateRequest.age());
            changes = true;
        }

        if (updateRequest.gender() != null && !updateRequest.gender().equals(customer.getGender())){
            customer.setGender(updateRequest.gender());
            changes = true;
        }

        if (updateRequest.email() != null && !updateRequest.email().equals(customer.getEmail())){
            if (customerDao.existsCustomerByEmail(updateRequest.email())){
                throw new DuplicateResourceException("email already taken");
            }
            customer.setEmail(updateRequest.email());
            changes = true;
        }

        if (!changes){
            throw new RequestValidationException("no data changes found");
        }
        customerDao.updateCustomer(customer);
    }
}
