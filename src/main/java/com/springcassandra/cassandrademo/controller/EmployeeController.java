package com.springcassandra.cassandrademo.controller;

import ch.qos.logback.core.model.TimestampModel;
import com.datastax.oss.driver.api.core.time.TimestampGenerator;
import com.github.javafaker.Faker;
import com.google.gson.Gson;
import com.springcassandra.cassandrademo.model.AppConfigData;
import com.springcassandra.cassandrademo.model.Employee;
import com.springcassandra.cassandrademo.repository.ConfigRepository;
import com.springcassandra.cassandrademo.repository.EmployeeRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.models.annotations.OpenAPI31;
import jnr.posix.Times;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.core.query.CassandraPageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/")
@Tag(name = "Employees", description = "Cassandra CRUD APIs for Employee Management")
public class EmployeeController {
    Logger log = LoggerFactory.getLogger(EmployeeController.class);

    @Autowired
    private ConfigRepository configRepository;
    //    private final int MAX_SALARY = 100000;
//    private final int MIN_SALARY = 10000;
//    private final int MIN_AGE = 22;
//    private final int MAX_AGE = 60;
    Faker faker = new Faker();
    @Autowired
    private CassandraTemplate cassandraTemplate;
    @Autowired
    private Gson gson;
    @Autowired
    private EmployeeRepository repository;

    @GetMapping("configurations-test-api")
    @Operation(summary = "This api can get all the current configurations of the app.")
    public ResponseEntity<AppConfigData> configurations() {
        Optional<AppConfigData> appConfigData = configRepository.findById(1);
        if (!appConfigData.isEmpty()) {
            return ResponseEntity.ok(appConfigData.get());
        } else return new ResponseEntity<>(new AppConfigData(), HttpStatus.NOT_FOUND);
    }

    @GetMapping("employees")
    @Operation(summary = "This API can get all the employees in the DB.")
    public ResponseEntity<List<Employee>> findSliceOfEmployees() {
        return ResponseEntity.ok(repository.findAll(
        ));
    }

    @PostMapping("add-employee")
    @Operation(summary = "This API is used to add an employee to the DB.")
    public ResponseEntity<String> addEmployee(@RequestBody Employee employee) {
        try {
            log.info("Saving new employee");
            repository.save(employee);
            return ResponseEntity.ok("Employee Saved successfully!");
        } catch (Exception e) {
            log.error("Exception occured: " + e);
        }
        return ResponseEntity.badRequest().body("Bad Credentials or an unknown exception occured.");

    }

    // task no 6 - Pagination in Spring data Cassandra work in 'Forward Only' basis like the way [iterable] interface works.
    @GetMapping("employees-by-page/{page}")
    @Operation(summary = "This API gets the asked page number of employees")
    public ResponseEntity<List<Employee>> findPageOfUsers(@PathVariable("page") int page) {
        int currPage = 0, size = 20;
        Slice<Employee> employeeSlice = repository.findAll(CassandraPageRequest.first(20));
        while (employeeSlice.hasNext() && currPage < page) {
            employeeSlice = repository.findAll(employeeSlice.nextPageable());
            currPage++;
        }
        System.out.println(employeeSlice.getContent());
        return ResponseEntity.ok(employeeSlice.getContent());

    }

    // PAT for github -> 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
    // task no 1 - insert n employees to cassandra db
    @PostMapping("add-bulk-employees")
    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Operation(summary = "This API can insert any defined no of fake employees into the DB.")
    public ResponseEntity<String> addBulkEmployees() {
        Long NO_OF_ENTRIES = configRepository.findNoOfInsertsById(1);
        try {
            log.info("Starting bulk insertion");
            long start = System.currentTimeMillis();
            for (int i = 0; i < NO_OF_ENTRIES; i++) {
                try {
                    Employee employee = createFakeEmployee();
                    try {
                        log.info("Inserting data for uuid: " + employee.getUuid() + " and the data being inserted is: " + gson.toJson(employee));
                        cassandraTemplate.insert(employee);
                    }catch (Exception e) {
                        log.error("Data could not be inserted due to exception: " + e);
                    }
                } catch (Exception e) {
                    log.error("Unknown exception occured: " + e);
                }
            }
            long end = System.currentTimeMillis();
            log.info("This activity took: " + (end - start) + " milliseconds");
            return ResponseEntity.ok("Insertion of " + NO_OF_ENTRIES + " performed successfully and this activity took " + (end - start) / 1000 + "seconds");
        } catch (Exception e) {
            log.error("Unknown exception occured.");
        }
        return ResponseEntity.internalServerError().body("Data could not be inserted properly.");
    }

    // task no 2 - Updating mobile numbers with even last digit
    @PutMapping("update-mobile")
    @Operation(summary = "This API is used for updating mobile numbers with even last digit - last digit is increased by 1")
    public void updateEvenPhoneNumbers() {
        Slice<Employee> employeeSlice = repository.findAll(CassandraPageRequest.first(100));
        while (employeeSlice.hasNext()) {
            for (int i = 0; i < 100; i++) {
                System.out.println("Old employee data: " + gson.toJson(employeeSlice.getContent().get(i)));
                String phoneNumber = employeeSlice.getContent().get(i).getPhoneNumber();
                int parseInt = Integer.parseInt(phoneNumber.substring(phoneNumber.length() - 1));
                if (parseInt % 2 == 0) {
                    phoneNumber = phoneNumber.substring(0, phoneNumber.length() - 1) + String.valueOf(parseInt + 1);
                    employeeSlice.getContent().get(i).setPhoneNumber(phoneNumber);
                    repository.insert(employeeSlice.getContent().get(i));
                    System.out.println("New employee data: " + gson.toJson(employeeSlice.getContent().get(i)));
                }
            }
            employeeSlice = repository.findAll(employeeSlice.nextPageable());
        }
    }

    // task no 3 - salary increment of all the employees by a fixed %
    @PutMapping("increase-salary")
    @Operation(summary = "This API can provide salary increment to all the employees by a fixed percentage (x%).")
    public void employeeSalaryIncrement() {
        Optional<AppConfigData> appConfigData = configRepository.findById(1);
        double INCREMENT_PERCENTAGE = appConfigData.get().getSalaryIncrementPercentage();
        Slice<Employee> employeeSlice = repository.findAll(CassandraPageRequest.first(10000));
        while (employeeSlice.hasNext()) {
            for (int i = 0; i < 10000; i++) {
                log.info("Old employee salary: " + employeeSlice.getContent().get(i).getSalary());
                employeeSlice.getContent().get(i).setSalary((long) (employeeSlice.getContent().get(i).getSalary() * (1 + (INCREMENT_PERCENTAGE / 100))));
                repository.insert(employeeSlice.getContent().get(i));
                log.info("New employee salary: " + employeeSlice.getContent().get(i).getSalary());
            }
            employeeSlice = repository.findAll(employeeSlice.nextPageable());
        }
    }

    // task no 4 - fetch the employee by mobile no
    @OpenAPI31
    @Operation(summary = "Find Employee details by Mobile No")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Employee found successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Employee.class))),
            @ApiResponse(responseCode = "404", description = "Employee not found")})
    @GetMapping("search-by-mobile/{mobile}")
    public ResponseEntity<List<Employee>> searchUsingMobileNo(@PathVariable("mobile") String mobile) {
        List<Employee> employees = new ArrayList<>();
        try {
            employees = repository.findByPhoneNumber(mobile);
            return ResponseEntity.ok(employees);
        } catch (Exception e) {
            log.error("Employee data could not be retrieved using this mobile no.");
        }
        return new ResponseEntity<>(employees, HttpStatus.NOT_FOUND);
    }

    // task no 5 - fetch the employee by name
    @OpenAPI31
    @Operation(summary = "Find Employee details by Name")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Employee found successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Employee.class))),
            @ApiResponse(responseCode = "404", description = "Employee not found")})
    @GetMapping("search-by-name/{name}")
    public ResponseEntity<List<Employee>> searchUsingName(@PathVariable("name") String name) {
        try {
            List<Employee> employee = repository.findByName(name);
            return ResponseEntity.ok(employee);
        } catch (Exception e) {
            log.error("No employee found by this name! Exception occured is: " + e);
        }
        return null;
    }

    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    private Employee createFakeEmployee() {
        String name = faker.name().fullName();
        String address = faker.address().buildingNumber() + ", " + faker.address().streetAddress() + ", " + faker.address().city() + ", " + faker.address().stateAbbr() + ", " + faker.address().country();
        String phoneNumber = faker.phoneNumber().cellPhone().toString();
        return new Employee(name, phoneNumber, address);
    }

    private String createFakeAddress(Faker faker) {
        String buildingAddress = faker.address().buildingNumber();
        String streetAddress = faker.address().streetAddress();
        String city = faker.address().cityName();
        String state = faker.address().state();

        String completeAddress = buildingAddress + ", " + streetAddress + ", " + city + ", " + state;
        return completeAddress;
    }

//    @DeleteMapping("delete/{deleteId}")
//    public ResponseEntity<String> deleteEmployee(@PathVariable("deleteId") long id) {
//        if (!repository.existsById(id)) {
//            return new ResponseEntity<>("Employee doesn't exist", HttpStatus.NOT_FOUND);
//        }
//        repository.deleteById(id);
//        return new ResponseEntity<>("Employee deleted!", HttpStatus.OK);
//    }
}
