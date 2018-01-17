package com.melayer.grapper.repository;

import com.melayer.grapper.domain.User;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB repository for the User entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserRepository extends MongoRepository<User, String> {

    User findByEmailIdAndPassword(String emailId,String password);

    User findByEmailIdAndMobileNo(String emailId,String mobileNo);

}
