package com.melayer.grapper.repository;

import com.melayer.grapper.domain.Admin;
import org.springframework.stereotype.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB repository for the Admin entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AdminRepository extends MongoRepository<Admin, String> {
        Admin findByEmailIdAndPassword(String emailId,String password);
}
