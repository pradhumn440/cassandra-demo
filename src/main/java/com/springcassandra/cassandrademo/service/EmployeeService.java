//package com.springcassandra.cassandrademo.service;
//
//import com.github.javafaker.Faker;
//import com.google.gson.Gson;
//import com.springcassandra.cassandrademo.model.Employee;
//import com.springcassandra.cassandrademo.repository.EmployeeRepository;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.cassandra.core.CassandraTemplate;
//import org.springframework.stereotype.Service;
//
//import java.sql.Timestamp;
//import java.util.concurrent.CompletableFuture;
//
//@Service
//public class EmployeeService {
//
//    Logger log = LoggerFactory.getLogger(EmployeeService.class);
//
//    Faker faker = new Faker();
//
//    @Autowired
//    private CassandraTemplate cassandraTemplate;
//
//    @Autowired
//    private EmployeeRepository employeeRepository;
//
//    @Autowired
//    private Gson gson;
//
//    public CompletableFuture<String> addBulkEmps(long NO_OF_ENTRIES) {
//        for (int i = 0; i < NO_OF_ENTRIES; i++) {
//            try {
//                Employee employee = createFakeEmployee();
//                try {
//                    log.info("Inserting data for uuid: " + employee.getUuid() + " and the data being inserted is: " + gson.toJson(employee));
//                    cassandraTemplate.insert(employee);
//                    return Z
//                } catch (Exception e) {
//                    log.error("Data could not be inserted due to exception: " + e);
//                }
//            } catch (Exception e) {
//                log.error("Unknown exception occured: " + e);
//            }
//        }
//
//    }
//
//    private Employee createFakeEmployee() {
//        String name = faker.name().fullName();
//        String address = faker.address().buildingNumber() + ", " + faker.address().streetAddress() + ", " + faker.address().city() + ", " + faker.address().stateAbbr() + ", " + faker.address().country();
//        String phoneNumber = faker.phoneNumber().cellPhone().toString();
//        return new Employee(name, phoneNumber, address, new Timestamp(System.currentTimeMillis()));
//    }
//}
