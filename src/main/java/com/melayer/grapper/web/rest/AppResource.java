package com.melayer.grapper.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.melayer.grapper.domain.App;

import com.melayer.grapper.repository.AppRepository;
import com.melayer.grapper.repository.search.AppSearchRepository;
import com.melayer.grapper.web.rest.errors.BadRequestAlertException;
import com.melayer.grapper.storage.StorageService;
import com.melayer.grapper.web.rest.util.DateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.net.URISyntaxException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * REST controller for managing App.
 */
@RestController
@CrossOrigin("*")
@RequestMapping("/api")
public class AppResource {

    private final Logger log = LoggerFactory.getLogger(AppResource.class);

    private static final String ENTITY_NAME = "app";

    private final AppRepository appRepository;

    private final AppSearchRepository appSearchRepository;

    public AppResource(AppRepository appRepository, AppSearchRepository appSearchRepository) {
        this.appRepository = appRepository;
        this.appSearchRepository = appSearchRepository;
    }

    @Autowired
    private StorageService storageService;

    public static final String STATUS="status";
    public static final String FAIL="failure";
    public static final String SUCCESS="success";
    public static final String MESSAGE="msg";
    public static final String RESULT="result";
    public static final String EXCEPTION="exception";

    Map<String,Object> map=null;
    ResponseEntity<Map<String,Object>> entity=null;
    /**
     * POST  /apps : Create a new app.
     *
     * @param app the app to create
     * @return the ResponseEntity with status 201 (Created) and with body the new app, or with status 400 (Bad Request) if the app has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/app/saveApp")
    @Timed
    public ResponseEntity<?> createApp(@Valid @RequestBody App app) throws URISyntaxException {
        try {
            map=new HashMap<>();
            if (app.getId() != null) {
                throw new BadRequestAlertException("A new app cannot already have an ID", ENTITY_NAME, "id exists");
            }
            app.setDate(DateFormat.getFormatedDate(System.currentTimeMillis()));
            appRepository.save(app);
            map.put(STATUS, SUCCESS);
            map.put(MESSAGE, "App Save Successfully");
            map.put(RESULT, app);
            entity = new ResponseEntity<>(map, HttpStatus.OK);

        }
        catch (Exception ex){
            map.put(STATUS,FAIL);
            map.put(EXCEPTION,ex);
            entity=new ResponseEntity<>(map,HttpStatus.BAD_REQUEST);
        }
        return entity;
    }

    /**
     * PUT  /apps : Updates an existing app.
     *
     * @return the ResponseEntity with status 200 (OK) and with body the updated app,
     * or with status 400 (Bad Request) if the app is not valid,
     * or with status 500 (Internal Server Error) if the app couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     * */
    @PostMapping("/app/uploadAppImage")
    @Timed
    public ResponseEntity<?> updateApp(@RequestParam String id,@RequestParam MultipartFile image) throws URISyntaxException {
        try {
            map=new HashMap<>();
            App app=appRepository.findById(id);
            System.out.println("appId"+app.getId());
            if (app.getId() == null) {
                throw new BadRequestAlertException("App is not valid", ENTITY_NAME, "id not exists");
            }
            String file=System.currentTimeMillis()+"_"+image.getOriginalFilename();
            storageService.store(image,file);
            app.setImage(file);
            appRepository.save(app);
            map.put(STATUS, SUCCESS);
            map.put(MESSAGE, "Image Uploaded Successfully");
            map.put(RESULT, app);
            entity = new ResponseEntity<>(map, HttpStatus.OK);
        }
        catch (Exception ex){
            map.put(STATUS,FAIL);
            map.put(EXCEPTION,ex);
            entity=new ResponseEntity<>(map,HttpStatus.BAD_REQUEST);
        }
        return entity;
    }

    /**
     * GET  /apps : get all the apps.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of apps in body
     */
    @GetMapping("/app/getApps")
    @Timed
    public ResponseEntity<?> getAllApps() {
        try {
            map=new HashMap<>();
            List<App> apps=appRepository.findAll(new Sort(Sort.Direction.DESC,"offer"));
            map.put(STATUS, SUCCESS);
            map.put(RESULT, apps);
            entity = new ResponseEntity<>(map, HttpStatus.OK);
        }
        catch (Exception ex){
            map.put(STATUS,FAIL);
            map.put(EXCEPTION,ex);
            entity=new ResponseEntity<>(map,HttpStatus.BAD_REQUEST);
        }
        return entity;
        }

    /**
     * GET  /apps/:id : get the "id" app.
     *
     * @param id the id of the app to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the app, or with status 404 (Not Found)
     */
    @GetMapping("/app/getApp/{id}")
    @Timed
    public ResponseEntity<?> getApp(@PathVariable String id) {
        try {
            map=new HashMap<>();
            App app=appRepository.findOne(id);
            map.put(STATUS, SUCCESS);
            map.put(RESULT, app);
            entity = new ResponseEntity<>(map, HttpStatus.OK);
        }
        catch (Exception ex){
            map.put(STATUS,FAIL);
            map.put(EXCEPTION,ex);
            entity=new ResponseEntity<>(map,HttpStatus.BAD_REQUEST);
        }
        return entity;
    }

    @GetMapping("/app/getPageApps/{page}/{size}")
    public Page<App> getAll(@PathVariable int page, @PathVariable int size) {
        PageRequest pageable = new PageRequest(page, size); //get 5 profiles on a page
        Page<App> jobDescriptionPage = appRepository.findAll(pageable);

        return jobDescriptionPage;
    }
    /**
     * DELETE  /apps/:id : delete the "id" app.
     *
     * @param id the id of the app to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/app/deleteApp/{id}")
    @Timed
    public ResponseEntity<?> deleteApp(@PathVariable String id) {
        try {
            map=new HashMap<>();
            appRepository.delete(id);
            map.put(STATUS, SUCCESS);
            map.put(MESSAGE,"App deleted Successfully");
            map.put(RESULT,"true");
            entity = new ResponseEntity<>(map, HttpStatus.OK);
        }
        catch (Exception ex){
            map.put(STATUS,FAIL);
            map.put(EXCEPTION,ex);
            entity=new ResponseEntity<>(map,HttpStatus.BAD_REQUEST);
        }
        return entity;
    }

    /**
     * SEARCH  /_search/apps?query=:query : search for the app corresponding
     * to the query.
     *
//     * @param query the query of the app search
     * @return the result of the search
     */

    @GetMapping("/app/searchApp/{searchName}")
    @Timed
    public ResponseEntity<?> searchApps(@PathVariable String searchName) {
        try {
            map=new HashMap<>();
            App app=appRepository.find(searchName);
            map.put(STATUS, SUCCESS);
            map.put(RESULT, app);
            entity = new ResponseEntity<>(map, HttpStatus.OK);
        }
        catch (Exception ex){
            map.put(STATUS,FAIL);
            map.put(EXCEPTION,ex);
            entity=new ResponseEntity<>(map,HttpStatus.BAD_REQUEST);
        }
        return entity;
    }

}
