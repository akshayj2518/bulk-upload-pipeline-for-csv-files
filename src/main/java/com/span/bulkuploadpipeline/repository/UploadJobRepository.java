package com.span.bulkuploadpipeline.repository;

import com.span.bulkuploadpipeline.model.UploadJob;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UploadJobRepository extends MongoRepository<UploadJob, String> {
}
