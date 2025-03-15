package com.interview.productspace.ScrapeJobs;

import com.interview.productspace.DTOs.JobDto;

import java.util.List;

public interface IScrapeJobs {
    static final long REFRESH_INTERVAL_HOURS = 24;

    List<JobDto> scrapeJob(String title);
}
