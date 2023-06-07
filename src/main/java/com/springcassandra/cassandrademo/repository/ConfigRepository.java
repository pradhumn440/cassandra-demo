package com.springcassandra.cassandrademo.repository;

import com.springcassandra.cassandrademo.model.AppConfigData;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigRepository extends CassandraRepository<AppConfigData, Integer> {

    @Query("select no_of_inserts from configdata where id =:id")
    public long findNoOfInsertsById(int id);

    @Query("select last_created_id from configdata where id =:id")
    public long findLastCreatedIdById(int id);
}
