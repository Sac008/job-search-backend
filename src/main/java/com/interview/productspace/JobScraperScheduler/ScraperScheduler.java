package com.interview.productspace.JobScraperScheduler;

import com.interview.productspace.Repository.JobEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.interview.productspace.services.ScraperService;

import java.util.Arrays;
import java.util.List;

@Component
public class ScraperScheduler {

    @Autowired
    JobEntityRepository jobEntityRepository;

    @Scheduled(fixedRate = 86400000)
    public void scheduledScraper() {
        this.jobEntityRepository.deleteAll();
    }
}
