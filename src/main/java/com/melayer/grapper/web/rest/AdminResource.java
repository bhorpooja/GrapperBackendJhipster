package com.melayer.grapper.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.melayer.grapper.domain.Admin;

import com.melayer.grapper.repository.AdminRepository;
import com.melayer.grapper.repository.search.AdminSearchRepository;
import com.melayer.grapper.web.rest.errors.BadRequestAlertException;
import com.melayer.grapper.web.rest.util.Credential;
import com.melayer.grapper.web.rest.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URISyntaxException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Admin.
 */
@RestController
@RequestMapping("/api")
public class AdminResource {

    private final Logger log = LoggerFactory.getLogger(AdminResource.class);

    private static final String ENTITY_NAME = "admin";

    private final AdminRepository adminRepository;

    private final AdminSearchRepository adminSearchRepository;

    public AdminResource(AdminRepository adminRepository, AdminSearchRepository adminSearchRepository) {
        this.adminRepository = adminRepository;
        this.adminSearchRepository = adminSearchRepository;
    }

    public static final String STATUS="status";
    public static final String FAIL="failure";
    public static final String SUCCESS="success";
    public static final String MESSAGE="msg";
    public static final String RESULT="result";
    public static final String EXCEPTION="exception";

    Map<String,Object> map=null;
    ResponseEntity<Map<String,Object>> entity=null;

    /**
     * POST  /admins : Create a new admin.
     *
     * @param admin the admin to create
     * @return the ResponseEntity with status 201 (Created) and with body the new admin, or with status 400 (Bad Request) if the admin has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/admin/registerAdmin")
    @Timed
    public ResponseEntity<?> createAdmin(@Valid @RequestBody Admin admin) throws URISyntaxException {
        try {
            map=new HashMap<>();
            if (admin.getId() != null) {
                throw new BadRequestAlertException("A new admin cannot already have an ID", ENTITY_NAME, "id exists");
            }
            adminRepository.save(admin);
            map.put(STATUS, SUCCESS);
            map.put(MESSAGE, "Admin Register Successfully");
            map.put(RESULT, admin);
            entity = new ResponseEntity<>(map, HttpStatus.OK);

        }
        catch (Exception ex){
            map.put(STATUS,FAIL);
            map.put(EXCEPTION,ex);
            entity=new ResponseEntity<>(map,HttpStatus.BAD_REQUEST);
        }
        return entity;
    }

    @PostMapping("/admin/adminLogin")
    public ResponseEntity<?> adminLogin(@RequestBody Credential credential){
        try {
            map=new HashMap<>();
            Admin admin = adminRepository.findByEmailIdAndPassword(credential.getEmailId(), credential.getPassword());
            if (admin != null) {
                map.put(STATUS, SUCCESS);
                map.put(MESSAGE, "Login Successful");
                map.put(RESULT, admin);
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
     * PUT  /admins : Updates an existing admin.
     *
     * @param admin the admin to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated admin,
     * or with status 400 (Bad Request) if the admin is not valid,
     * or with status 500 (Internal Server Error) if the admin couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/admins")
    @Timed
    public ResponseEntity<?> updateAdmin(@Valid @RequestBody Admin admin) throws URISyntaxException {
        log.debug("REST request to update Admin : {}", admin);
        if (admin.getId() == null) {
            return createAdmin(admin);
        }
        Admin result = adminRepository.save(admin);
        adminSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, admin.getId().toString()))
            .body(result);
    }

    /**
     * GET  /admins : get all the admins.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of admins in body
     */
    @GetMapping("/admins")
    @Timed
    public List<Admin> getAllAdmins() {
        log.debug("REST request to get all Admins");
        return adminRepository.findAll();
        }

    /**
     * GET  /admins/:id : get the "id" admin.
     *
     * @param id the id of the admin to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the admin, or with status 404 (Not Found)
     */
    @GetMapping("/admins/{id}")
    @Timed
    public ResponseEntity<Admin> getAdmin(@PathVariable String id) {
        log.debug("REST request to get Admin : {}", id);
        Admin admin = adminRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(admin));
    }

    /**
     * DELETE  /admins/:id : delete the "id" admin.
     *
     * @param id the id of the admin to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/admins/{id}")
    @Timed
    public ResponseEntity<Void> deleteAdmin(@PathVariable String id) {
        log.debug("REST request to delete Admin : {}", id);
        adminRepository.delete(id);
        adminSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id)).build();
    }

    /**
     * SEARCH  /_search/admins?query=:query : search for the admin corresponding
     * to the query.
     *
     * @param query the query of the admin search
     * @return the result of the search
     */
    @GetMapping("/_search/admins")
    @Timed
    public List<Admin> searchAdmins(@RequestParam String query) {
        log.debug("REST request to search Admins for query {}", query);
        return StreamSupport
            .stream(adminSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }

}
