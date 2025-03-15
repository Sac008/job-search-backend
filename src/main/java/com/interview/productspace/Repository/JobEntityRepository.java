package com.interview.productspace.Repository;

import com.interview.productspace.models.JobEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface JobEntityRepository extends JpaRepository<JobEntity, Long> {

    @Query("SELECT MAX(j.scrapedAt) FROM JobEntity j")
    LocalDateTime findLatestScrapedTime();

    JobEntity findJobEntityByTitleContaining(String title);

    List<JobEntity> findAllByTitleContaining(String title);
}
