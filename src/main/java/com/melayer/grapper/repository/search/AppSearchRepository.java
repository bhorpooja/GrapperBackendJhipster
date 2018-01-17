package com.melayer.grapper.repository.search;

import com.melayer.grapper.domain.App;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the App entity.
 */
public interface AppSearchRepository extends ElasticsearchRepository<App, String> {
}
