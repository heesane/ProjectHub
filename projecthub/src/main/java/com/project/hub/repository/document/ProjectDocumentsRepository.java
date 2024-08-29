package com.project.hub.repository.document;

import com.project.hub.model.documents.ProjectDocuments;
import java.util.Optional;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ProjectDocumentsRepository extends
    ElasticsearchRepository<ProjectDocuments, Long> {

  Optional<ProjectDocuments> findByTitle(String title);

}
