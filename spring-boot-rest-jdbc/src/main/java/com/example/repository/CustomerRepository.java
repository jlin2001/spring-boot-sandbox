package com.example.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import com.example.domain.Customer;

@Repository
public class CustomerRepository {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    public List<Customer> findAll() {

        return jdbcTemplate.query(
                "SELECT * FROM customers ORDER BY id",
                new BeanPropertyRowMapper<Customer>(Customer.class));
    }

    public Customer findOne(Integer id) {

        SqlParameterSource param = new MapSqlParameterSource().addValue("id", id);

        return jdbcTemplate.queryForObject(
                "SELECT * FROM customers WHERE id = :id",
                param,
                new BeanPropertyRowMapper<Customer>(Customer.class));
    }

    public Customer save(Customer customer) {

        SqlParameterSource param = new BeanPropertySqlParameterSource(customer);

        if (customer.getId() == null) {

            SimpleJdbcInsert insert =
                    new SimpleJdbcInsert((JdbcTemplate) jdbcTemplate.getJdbcOperations())
                            .withTableName("customers")
                            .usingGeneratedKeyColumns("id");

            Number key = insert.executeAndReturnKey(param);
            customer.setId(key.intValue());
        } else {
            jdbcTemplate.update("UPDATE customers SET name = :name, address = :address WHERE id = :id", param);
        }

        return customer;
    }

    public void delete(Integer id) {

        SqlParameterSource param = new MapSqlParameterSource().addValue("id", id);

        jdbcTemplate.update(
                "DELETE FROM customers WHERE id = :id",
                param);
    }
}
