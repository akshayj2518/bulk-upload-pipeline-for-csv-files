package com.span.bulkuploadpipeline.repository;

import com.span.bulkuploadpipeline.model.Goal;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GoalRepository extends MongoRepository<Goal, String> {
    // CRUD operations inherited
}
