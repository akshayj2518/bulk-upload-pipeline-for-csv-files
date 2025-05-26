package com.span.bulkuploadpipeline.controller;

import com.span.bulkuploadpipeline.model.JobStatus;
import com.span.bulkuploadpipeline.model.UploadJob;
import com.span.bulkuploadpipeline.repository.UploadJobRepository;
import com.span.bulkuploadpipeline.service.FileProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Controller
public class UploadController {

    private static final String UPLOAD_DIR = "/tmp/uploads/";

    @Autowired
    private UploadJobRepository jobRepository;

    @Autowired
    private FileProcessingService fileProcessingService;

    // Serve the HTML page
    @GetMapping("/")
    public String showUploadPage(Model model) {
        List<UploadJob> jobs = jobRepository.findAll();
        model.addAttribute("jobs", jobs);
        return "upload"; // Thymeleaf template name: src/main/resources/templates/upload.html
    }

    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, Model model) {
        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        if (filename == null || !filename.toLowerCase().endsWith(".csv")) {
            model.addAttribute("error", "Only CSV files are allowed.");
            model.addAttribute("jobs", jobRepository.findAll());
            return "upload";
        }

        if (file.getSize() > 10 * 1024 * 1024) {
            model.addAttribute("error", "File size exceeds 10MB limit.");
            model.addAttribute("jobs", jobRepository.findAll());
            return "upload";
        }

        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) uploadDir.mkdirs();

        String jobId = UUID.randomUUID().toString();
        Path filePath = Paths.get(UPLOAD_DIR, jobId + ".csv");

        try {
            Files.copy(file.getInputStream(), filePath);

            UploadJob job = new UploadJob();
            job.setJobId(jobId);
            job.setFilePath(filePath.toString());
            job.setStatus(JobStatus.PENDING);
            job.setCreatedAt(LocalDateTime.now());
            job.setUpdatedAt(LocalDateTime.now());
            jobRepository.save(job);

            fileProcessingService.processFile(job);

            model.addAttribute("success", "Job submitted successfully. Job ID: " + jobId);

        } catch (IOException e) {
            model.addAttribute("error", "Failed to save file: " + e.getMessage());
        }

        model.addAttribute("jobs", jobRepository.findAll());
        return "upload";
    }

    @GetMapping("/jobs")
    @ResponseBody
    public List<UploadJob> getAllJobs() {
        return jobRepository.findAll();
    }

    @GetMapping("/api/jobs")
    @ResponseBody
    public List<UploadJob> getJobStatuses() {
        return jobRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }
}
