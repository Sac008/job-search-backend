package com.interview.productspace.controller;

import com.interview.productspace.DTOs.JobDto;
import com.interview.productspace.models.JobEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.interview.productspace.services.ScraperService;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/jobs")
@CrossOrigin(origins = "*")
public class JobController {

    @Autowired
    ScraperService scraperService;

    @GetMapping("/scrape")
    public ResponseEntity<?> scrape(@RequestParam("jobTitle") String jobTitle) {
        try {
            List<JobDto> jobs = scraperService.getJobListings(jobTitle);
            if(jobs.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(jobs);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

}
