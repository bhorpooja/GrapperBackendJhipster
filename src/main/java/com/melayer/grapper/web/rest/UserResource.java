package com.melayer.grapper.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.melayer.grapper.domain.App;
import com.melayer.grapper.domain.User;

import com.melayer.grapper.repository.AppRepository;
import com.melayer.grapper.repository.UserRepository;
import com.melayer.grapper.repository.search.UserSearchRepository;
import com.melayer.grapper.web.rest.errors.BadRequestAlertException;
import com.melayer.grapper.web.rest.util.Credential;
import com.melayer.grapper.web.rest.util.DateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing User.
 */
@RestController
@RequestMapping("/api")
public class UserResource {

    private final Logger log = LoggerFactory.getLogger(UserResource.class);

    private static final String ENTITY_NAME = "user";

    private final UserRepository userRepository;

    private final UserSearchRepository userSearchRepository;

    public UserResource(UserRepository userRepository, UserSearchRepository userSearchRepository) {
        this.userRepository = userRepository;
        this.userSearchRepository = userSearchRepository;
    }

    @Autowired
    private AppResource appResource;

    @Autowired
    private AppRepository appRepository;

    public static final String STATUS="status";
    public static final String FAIL="failure";
    public static final String SUCCESS="success";
    public static final String MESSAGE="msg";
    public static final String RESULT="result";
    public static final String EXCEPTION="exception";

    Map<String,Object> map=null;
    ResponseEntity<Map<String,Object>> entity=null;

    /**
     * POST  /users : Create a new user.
     *
     * @param user the user to create
     * @return the ResponseEntity with status 201 (Created) and with body the new user, or with status 400 (Bad Request) if the user has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/user/registerUser")
    @Timed
    public ResponseEntity<?> createUser(@RequestBody User user) throws URISyntaxException {
        try {
            map=new HashMap<>();
            if (user.getId() != null) {
                throw new BadRequestAlertException("A new user cannot already have an ID", ENTITY_NAME, "id exists");
            }
            else {
                User user1=userRepository.findByEmailIdAndMobileNo(user.getEmailId(),user.getMobileNo());
                if(user1==null) {
                    user.setDate(DateFormat.getFormatedDate(System.currentTimeMillis()));
                    userRepository.save(user);
                    map.put(STATUS, SUCCESS);
                    map.put(MESSAGE, "User Register Successfully");
                    map.put(RESULT, user);
                    entity = new ResponseEntity<>(map, HttpStatus.OK);
                }
                else {
                    map.put(STATUS, FAIL);
                    map.put(MESSAGE, "User Already Exist");
                    entity = new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
                }
            }
        }
        catch (Exception ex){
            map.put(STATUS,FAIL);
            map.put(EXCEPTION,ex);
            entity=new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        }
        return entity;

    }

    /**
     * PUT  /users : Updates an existing user.
     *
//     * @param user the user to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated user,
     * or with status 400 (Bad Request) if the user is not valid,
     * or with status 500 (Internal Server Error) if the user couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/user/updateUser/{id}")
    @Timed
    public ResponseEntity<?> updateUser(@PathVariable String id,@RequestBody User user) throws URISyntaxException {
        try {
            map=new HashMap<>();
            User user1=userRepository.findOne(id);
            System.out.println("USER ID :"+user1.getId());
            if (user1.getId() == null) {
                throw new BadRequestAlertException("A user is invalid", ENTITY_NAME, "id not exists");
            }
            user1.setInstalledApps(user.getInstalledApps());
            user1.setUninstalledApps(user.getUninstalledApps());
            user1.setSkippedApps(user.getSkippedApps());
            System.out.println(user1.getInstalledApps());
            userRepository.save(user1);
            map.put(STATUS, SUCCESS);
            map.put(MESSAGE, "User Updated Successfully");
            map.put(RESULT, user1);
            entity = new ResponseEntity<>(map, HttpStatus.OK);
        }
       catch (Exception ex){
            map.put(STATUS,FAIL);
            map.put(EXCEPTION,ex);
            entity=new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        }
        return entity;
    }

    @PostMapping("/user/userLogin")
    public ResponseEntity<?> userLogin(@RequestBody Credential credential){
        try {
            map=new HashMap<>();
            User user = userRepository.findByEmailIdAndPassword(credential.getEmailId(), credential.getPassword());
            if (user != null) {
                map.put(STATUS, SUCCESS);
                map.put(MESSAGE, "Login Successful");
                map.put(RESULT,user.getId());
                entity = new ResponseEntity<>(map, HttpStatus.OK);
            } else {
                map.put(STATUS, FAIL);
                map.put(MESSAGE, "Login Unsuccessful");
                entity = new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
            }
        }
        catch (Exception ex){
            map.put(STATUS,FAIL);
            map.put(EXCEPTION,ex);
            entity=new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        }
        return entity;
    }

    /**
     * GET  /users : get all the users.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of users in body
     */
    @GetMapping("/user/getUsers")
    @Timed
    public ResponseEntity<?> getAllUsers() {
        try {
            map=new HashMap<>();
            List<User> users=userRepository.findAll();
            map.put(STATUS, SUCCESS);
            map.put(RESULT, users);
            entity = new ResponseEntity<>(map, HttpStatus.OK);
        }
        catch (Exception ex){
            map.put(STATUS,FAIL);
            map.put(EXCEPTION,ex);
            entity=new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        }
        return entity;
        }

    /**
     * GET  /users/:id : get the "id" user.
     *
     * @param id the id of the user to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the user, or with status 404 (Not Found)
     */
    @GetMapping("/user/getUser/{id}")
    @Timed
    public ResponseEntity<?> getUser(@PathVariable String id) {
        try {
            map=new HashMap<>();
            User user=userRepository.findOne(id);
            map.put(STATUS, SUCCESS);
            map.put(RESULT, user);
            entity = new ResponseEntity<>(map, HttpStatus.OK);
        }
        catch (Exception ex){
            map.put(STATUS,FAIL);
            map.put(EXCEPTION,ex);
            entity=new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        }
        return entity;
    }

    @GetMapping("/user/getUserAppsCount/{id}")
    public ResponseEntity<?> getInstalledApps(@PathVariable String id) {
        try {
            map=new HashMap<>();
            User user=userRepository.findOne(id);
            List<String> installed=user.getInstalledApps();
            List<String> uninstalled=user.getUninstalledApps();
            map.put(STATUS, SUCCESS);
            map.put(MESSAGE,"count of installed & uninstalled Apps");
            map.put(RESULT, installed.size()+ " & " +uninstalled.size());
            entity = new ResponseEntity<>(map, HttpStatus.OK);
        }
        catch (Exception ex){
            map.put(STATUS,FAIL);
            map.put(EXCEPTION,ex);
            entity=new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        }
        return entity;
    }

    @GetMapping("/user/getUserApps")
    public ResponseEntity<?> getUserApp(){
        try{
            map =new HashMap<>();
            List<App> apps=appRepository.findAll();
            map.put(STATUS,SUCCESS);
            map.put(RESULT,apps);
            entity=new ResponseEntity(map,HttpStatus.OK);
        }
        catch (Exception ex){
            map.put(STATUS,FAIL);
            map.put(EXCEPTION,ex);
            entity=new ResponseEntity(map,HttpStatus.BAD_REQUEST);
        }
        return entity;
    }


    /**
     * DELETE  /users/:id : delete the "id" user.
     *
     * @param id the id of the user to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/user/deleteUser/{id}")
    @Timed
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        try {
            map=new HashMap<>();
            userRepository.delete(id);
            map.put(STATUS, SUCCESS);
            map.put(MESSAGE,"User Deleted Successfully");
            entity = new ResponseEntity<>(map, HttpStatus.OK);
        }
        catch (Exception ex){
            map.put(STATUS,FAIL);
            map.put(EXCEPTION,ex);
            entity=new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        }
        return entity;
    }

    /**
     * SEARCH  /_search/users?query=:query : search for the user corresponding
     * to the query.
     *
     * @param query the query of the user search
     * @return the result of the search
     */
    @GetMapping("/_search/users")
    @Timed
    public List<User> searchUsers(@RequestParam String query) {
        log.debug("REST request to search Users for query {}", query);
        return StreamSupport
            .stream(userSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }

}
