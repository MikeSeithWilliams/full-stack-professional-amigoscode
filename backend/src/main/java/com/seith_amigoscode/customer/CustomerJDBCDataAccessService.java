package com.seith_amigoscode.customer;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("jdbc")
public class CustomerJDBCDataAccessService implements CustomerDao{

    private final JdbcTemplate jdbcTemplate;
    private final CustomerRowMapper customerRowMapper;

    public CustomerJDBCDataAccessService(JdbcTemplate jdbcTemplate, CustomerRowMapper customerRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.customerRowMapper = customerRowMapper;
    }

    @Override
    public List<Customer> selectAllCustomers() {
        var sql = """
                SELECT id, name, email, password, age, gender, profile_image_id
                FROM customer
                LIMIT 1000
                """;

        return jdbcTemplate.query(sql, customerRowMapper);
    }

    @Override
    public Optional<Customer> selectCustomerById(Long customerId) {
        var sql = """
                SELECT id, name, email, password, age, gender, profile_image_id
                FROM customer
                WHERE id = ?
                """;
        return jdbcTemplate.query(sql, customerRowMapper, customerId)
                .stream()
                .findFirst();
    }

    @Override
    public void insertCustomer(Customer customer) {
        var sql = """
                INSERT INTO customer (name, email, password, age, gender)
                VALUES (?, ?, ?, ?, ?)
                """;
        jdbcTemplate.update(
                 sql,
                 customer.getName(),
                 customer.getEmail(),
                 customer.getPassword(),
                 customer.getAge(),
                 customer.getGender().name()
        );
    }

    @Override
    public boolean existsCustomerByEmail(String email) {
        var sql = """
                SELECT count(id)
                FROM customer
                WHERE email = ?
                """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }

    @Override
    public boolean existsCustomerById(Long Id) {
        var sql = """
                SELECT count(id)
                FROM customer
                WHERE id = ?
                """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, Id);
        return count != null && count == 1;
    }

    @Override
    public void deleteCustomerById(Long customerId) {
        var sql = """
                DELETE
                FROM customer
                WHERE id = ?
                """;
        jdbcTemplate.update(sql, customerId);
    }

    @Override
    public void updateCustomer(Customer customer) {

        if (customer.getName() != null) {
            var sql = """
                    UPDATE customer
                    SET name = ?
                    WHERE id = ?
                    """;
            jdbcTemplate.update(sql, customer.getName(), customer.getId());
        }
        if (customer.getEmail() != null) {
            var sql = """
                    UPDATE customer
                    SET email = ?
                    WHERE id = ?
                    """;
            jdbcTemplate.update(sql, customer.getEmail(), customer.getId());
        }
        if (customer.getAge() != null) {
            var sql = """
                    UPDATE customer
                    SET age = ?
                    WHERE id = ?
                    """;
            int result = jdbcTemplate.update(sql, customer.getAge(), customer.getId());
            System.out.println("jdbcTemplate.update age: " + result);
        }
        if (customer.getGender() != null) {
            var sql = """
                    UPDATE customer
                    SET gender = ?
                    WHERE id = ?
                    """;
            jdbcTemplate.update(sql, customer.getGender().name(), customer.getId());
        }

    }

    @Override
    public Optional<Customer> selectUserByEmail(String email) {
        var sql = """
                SELECT id, name, email, password, age, gender, profile_image_id
                FROM customer
                WHERE email = ?
                """;
        return jdbcTemplate.query(sql, customerRowMapper, email)
                .stream()
                .findFirst();
    }

    @Override
    public void updateCustomerProfileId(String profileImageId, Long customerId) {
        var sql = """
                UPDATE customer
                SET profile_image_id = ?
                WHERE id = ?
                """;
        jdbcTemplate.update(sql, profileImageId, customerId);
    }
}
