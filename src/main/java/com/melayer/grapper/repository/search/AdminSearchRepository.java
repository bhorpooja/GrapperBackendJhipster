package com.melayer.grapper.repository.search;

import com.melayer.grapper.domain.Admin;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Admin entity.
 */
public interface AdminSearchRepository extends ElasticsearchRepository<Admin, String> {
}
