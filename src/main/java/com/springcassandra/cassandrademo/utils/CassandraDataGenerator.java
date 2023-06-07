package com.springcassandra.cassandrademo.utils;

import com.github.javafaker.Faker;
import com.google.gson.Gson;
import com.opencsv.CSVWriter;
import com.springcassandra.cassandrademo.model.Employee;
import com.springcassandra.cassandrademo.repository.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
public class CassandraDataGenerator {

    Logger log = LoggerFactory.getLogger(CassandraDataGenerator.class);

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private Gson gson;
    private final int MAX_SALARY = 100000;
    private final int MIN_SALARY = 10000;
    private final int MIN_AGE = 22;
    private final int MAX_AGE = 60;
    private final int NO_OF_ENTRIES = 10000000;
    private int NO_OF_ENTRIES_PER_BATCH = 1500;
    public Long id = 1l;
    private static final String FIXED_DELAY = "5000";
    @Autowired
    private CassandraTemplate cassandraTemplate;
    private final ThreadPoolExecutor executor = new ThreadPoolExecutor(15, 15, 30, TimeUnit.SECONDS, new ArrayBlockingQueue<>(NO_OF_ENTRIES_PER_BATCH));


    File file = new File("/Users/pradhumn1.porwal/Downloads/apache-cassandra-3.11.14/bulk.csv");

//    @Scheduled(fixedRate = 6000)
    public void startGenerator() {
        log.info("Generator started!");
        long start = System.currentTimeMillis();
        List<CompletableFuture> futures = new ArrayList<>();
        while (id < NO_OF_ENTRIES_PER_BATCH && NO_OF_ENTRIES_PER_BATCH<NO_OF_ENTRIES) {
            Long finalId = id;
            futures.add(CompletableFuture.runAsync(() -> execute(finalId, file), executor));
            id += 1L;
        }
        for (CompletableFuture future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                log.error("Exception occured while insertion using future: {}", e);
            }
        }
        NO_OF_ENTRIES_PER_BATCH += NO_OF_ENTRIES_PER_BATCH;
        long end = System.currentTimeMillis();
        log.info("TOTAL TIME TAKEN FOR THIS ACTIVITY: {}", (end-start));
    }

    @Async
    public void execute(Long id, File file) {
        try {
            CSVWriter writer = new CSVWriter(new FileWriter(file));
            writer.writeNext(createFakeEmployee(id));
            log.info("Inserting into csv for id: " + id);
        } catch (Exception e) {
            log.error("Exception occured: " + e);
        }
    }

    private String[] createFakeEmployee(Long id) {
        Faker faker = new Faker();
        String name = faker.name().fullName();
        String address = createFakeAddress(faker);
        String phoneNumber = faker.phoneNumber().cellPhone().toString();
        String[] employee = new String[]{String.valueOf(id), address, String.valueOf(getRandomNumber(MIN_AGE, MAX_AGE)), name, phoneNumber, String.valueOf(getRandomNumber(MIN_SALARY, MAX_SALARY))};
        return employee;
    }

    private String createFakeAddress(Faker faker) {
        String buildingAddress = faker.address().buildingNumber();
        String streetAddress = faker.address().streetAddress();
        String city = faker.address().cityName();
        String state = faker.address().state();

        String completeAddress = buildingAddress + ", " + streetAddress + ", " + city + ", " + state;
        return completeAddress;
    }

    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
}
