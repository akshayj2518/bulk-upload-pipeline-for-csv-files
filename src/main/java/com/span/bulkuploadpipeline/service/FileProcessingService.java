package com.span.bulkuploadpipeline.service;

import com.span.bulkuploadpipeline.model.Goal;
import com.span.bulkuploadpipeline.model.JobStatus;
import com.span.bulkuploadpipeline.model.UploadJob;
import com.span.bulkuploadpipeline.repository.GoalRepository;
import com.span.bulkuploadpipeline.repository.UploadJobRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class FileProcessingService {

    @Autowired
    private UploadJobRepository jobRepository;

    @Autowired
    private GoalRepository goalRepository;

    private static final int QUEUE_CAPACITY = 10;
    BlockingQueue<Goal> queue = new LinkedBlockingQueue(QUEUE_CAPACITY);
    private static final int NUM_CONSUMERS = 2;
    private static final int BATCH_SIZE = 4;
    private static final Goal POISON_PILL = new Goal();

    private static final AtomicInteger successCount = new AtomicInteger(0);




    CountDownLatch countDownLatch = new CountDownLatch(NUM_CONSUMERS);

    ExecutorService consumerExecutor = Executors.newFixedThreadPool(2);

    @Async
    public void processFile(UploadJob job) {
        long startTime = System.currentTimeMillis();
        try {
            job.setStatus(JobStatus.PROCESSING);
            job.setUpdatedAt(LocalDateTime.now());
            jobRepository.save(job);

            for(int i=0; i<NUM_CONSUMERS; ++i) {
                consumerExecutor.submit(() -> {
                    List<Goal> batch = new ArrayList<>();

                    try {
                        while (true) {
                            Goal goal = queue.take();

                            if (goal == POISON_PILL)
                                break;

                            Thread.sleep(3000);
                            batch.add(goal);

                            if (batch.size() >= BATCH_SIZE) {
                                goalRepository.saveAll(batch);
                                successCount.addAndGet(batch.size());
                                batch.clear();
                            }
                        }

                        if (!batch.isEmpty())
                            successCount.addAndGet(batch.size());
                            goalRepository.saveAll(batch);
                    }
                    catch(InterruptedException exception){
                            Thread.currentThread().interrupt();
                        }
                    finally{
                            countDownLatch.countDown();;
                        }
                });
            }


//            int successCount = 0;
            int failureCount = 0;

            try (BufferedReader in = Files.newBufferedReader(Paths.get(job.getFilePath()), StandardCharsets.UTF_8))
                {
                Iterable<CSVRecord> records = CSVFormat.DEFAULT
                        .withFirstRecordAsHeader()
                        .parse(in);

                for (CSVRecord record : records) {
                    try {
                        String goalId = record.get(0);
                        String goalName = record.get(1);
                        String goalDescription = record.get(2);
                        String goalAssignedToPerson = record.get(3);

                        // Basic validation
                        if (goalId == null || goalId.isBlank() || goalName == null || goalName.isBlank()) {
                            failureCount++;
                            continue; // Skip invalid record
                        }

                        Goal goal = new Goal(goalId, goalName, goalDescription, goalAssignedToPerson);

                        queue.put(goal);


                    } catch (Exception ex) {
                        failureCount++;
                        // Optionally log ex.getMessage()
                    }
                }
                    for(int i=0; i<NUM_CONSUMERS; ++i)
                        queue.put(POISON_PILL);
            }

            countDownLatch.await();

            if (successCount.get() == 0) {
                job.setStatus(JobStatus.FAILED);
                job.setErrorMessage("No valid goals were created from the uploaded file.");
            } else {
                job.setStatus(JobStatus.COMPLETED);
            }

        } catch (Exception e) {
            job.setStatus(JobStatus.FAILED);
            job.setErrorMessage(e.getMessage());
        } finally {
            job.setUpdatedAt(LocalDateTime.now());
            jobRepository.save(job);
        }
        long endTime = System.currentTimeMillis();  // End timing here
        long duration = endTime - startTime;
        System.out.println("Total processing time: " + duration + " ms");

    }


}
