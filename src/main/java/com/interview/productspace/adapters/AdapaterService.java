package com.interview.productspace.adapters;

import com.interview.productspace.DTOs.JobDto;
import com.interview.productspace.models.JobEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdapaterService {

    public JobEntity convertDtoToEntity(JobDto job) {
        return JobEntity.builder().
                title(job.getTitle()).
                company(job.getCompany()).
                applicationLink(job.getApplicationLink()).
                location(job.getLocation()).
                experience(job.getExperience()).
                source(job.getSource()).
                scrapedAt(job.getScrapedAt()).build();
    }

    public List<JobDto> convertEntityToDto(List<JobEntity> jobs) {
        List<JobDto> jobDtos = new ArrayList<>();
        for(JobEntity job : jobs) {
            jobDtos.add(JobDto.builder().
                    title(job.getTitle()).
                    company(job.getCompany()).
                    applicationLink(job.getApplicationLink()).
                    location(job.getLocation()).
                    experience(job.getExperience()).
                    source(job.getSource()).
                    scrapedAt(job.getScrapedAt()).
                    build());
        }
        return jobDtos;
    }
}
