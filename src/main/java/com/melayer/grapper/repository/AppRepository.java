package com.melayer.grapper.repository;

import com.melayer.grapper.domain.App;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB repository for the App entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AppRepository extends MongoRepository<App, String> {

    App findById(String id);

    @Query("{$text:{$search:?0}}")
    App find(String searchName);
}
