package com.springcassandra.cassandrademo.service;

import com.google.gson.Gson;
import com.springcassandra.cassandrademo.kafka.KafkaProducer;
import com.springcassandra.cassandrademo.model.Employee;
import com.springcassandra.cassandrademo.repository.EmployeeRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KafkaPublishService {
    Logger logger = LoggerFactory.getLogger(KafkaPublishService.class);
    @Autowired
    private KafkaProducer kafkaProducer;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private Gson gson;

//    @PostConstruct
    public void publishCassandraData() {
        List<Employee> employeeList = employeeRepository.findAll();
        System.out.println(employeeList.size());
        for (Employee employee : employeeList) {
            logger.info("Publishing message for employeeUuid: {}", employee.getUuid());
            logger.info("Generated json for employeeUuid: {} is {}", employee.getUuid(), gson.toJson(employee));
            kafkaProducer.sendMessage(gson.toJson(employee));
            logger.info("Data published successfully for employeeUuid: {}", employee.getUuid());
        }
    }
}
