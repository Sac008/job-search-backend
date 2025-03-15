package com.interview.productspace.DTOs;


import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobDto {
    private String title;
    private String company;
    private String location;
    private String experience;
    private String applicationLink;
    private String source;
    private LocalDateTime scrapedAt;
}
