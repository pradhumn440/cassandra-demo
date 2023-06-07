package com.springcassandra.cassandrademo.repository;

import com.springcassandra.cassandrademo.model.Employee;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends CassandraRepository<Employee, Long> {

    Slice<Employee> findAll(Pageable pageable);

    @AllowFiltering
    List<Employee> findByPhoneNumber(String phonenumber);

    @AllowFiltering
    List<Employee> findByName(String name);
}
