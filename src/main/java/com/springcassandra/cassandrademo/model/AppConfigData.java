package com.springcassandra.cassandrademo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table(value = "configdata")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AppConfigData {
    @Column(value = "id")
    @PrimaryKey
    int id;

    @Column(value = "last_created_id")
    long lastCreatedId;

    @Column(value = "no_of_inserts")
    long noOfInserts;

    @Column(value = "salary_increment_percentage")
    double salaryIncrementPercentage;
}
