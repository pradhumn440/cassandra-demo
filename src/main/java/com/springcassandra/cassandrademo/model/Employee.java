package com.springcassandra.cassandrademo.model;

//import jakarta.persistence.*;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.UUID;

@Table
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Employee {

    @PrimaryKey
    @Column
    private UUID uuid;

    @Column
    private String name;

    @Column
    private int age;

    @Column(value = "phone_number")
    private String phoneNumber;

    @Column
    private long salary;

    @Column
    private String address;

//    @Column(value = "creation_timestamp")
//    private Timestamp creationTimestamp;

    public Employee(String name, String phoneNumber, String address) {
        this.age = 25;
        this.salary = 45000;
        this.address = address;
        this.uuid = Uuids.timeBased();
        this.name = name;
        this.phoneNumber = phoneNumber;
//        this.creationTimestamp = creationTimestamp;
    }
}
