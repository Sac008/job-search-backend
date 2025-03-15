package com.interview.productspace.ScrapeJobs;

import com.interview.productspace.DTOs.JobDto;
import com.interview.productspace.Repository.JobEntityRepository;
import com.interview.productspace.adapters.AdapaterService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class ScrapeNaukriJobs implements IScrapeJobs{


    @Autowired
    private JobEntityRepository jobEntityRepo;

    @Autowired
    private AdapaterService adapterService;
    private static final String NAUKRI_URL = "https://www.naukri.com/%s-jobs";

    @Override
    public List<JobDto> scrapeJob(String jobTitle) {

        /* Checking if data already exists or not */
        if(jobTitle.isEmpty()) return null;
        List<JobDto> existingJobs = adapterService.convertEntityToDto(jobEntityRepo.findAllByTitleContaining(jobTitle));
        if(!existingJobs.isEmpty()) return existingJobs;

        /* Formatting the url and creating a synchronized list */
        List<JobDto> jobs = Collections.synchronizedList(new ArrayList<>());
        String baseUrl = "https://www.naukri.com";
        String formattedUrl = String.format(NAUKRI_URL, jobTitle.replace(" ", "-"));

        try {
            /* creating the chrome driver that will get us the dynamically rendered content from chrome */
            WebDriver driver = new ChromeDriver();
            driver.get(formattedUrl);

            // Wait for the page to load
            Thread.sleep(3000);

            // Parse the first page
            Document doc = Jsoup.parse(driver.getPageSource());

            // Extract pagination links
            Elements paginationLinks = doc.select(".styles_pages__v1rAK a");
            Set<String> pageUrls = new LinkedHashSet<>(); // Use a Set to avoid duplicates
            int counter = 0;
            for (Element pageLink : paginationLinks) {
                String relativeUrl = pageLink.attr("href");
                if (!relativeUrl.isEmpty() && counter < 5) {
                    pageUrls.add(baseUrl + relativeUrl);
                }
                else{
                    break;
                }
            }

            // Add the first page URL (since it's not in the pagination links)
            pageUrls.add(formattedUrl);

            driver.quit(); // Close driver after getting pagination URLs

            // Multi-threaded scraping
            ExecutorService executor = Executors.newFixedThreadPool(5); // Use 5 threads
            List<Future<List<JobDto>>> futures = new ArrayList<>();

            for (String pageUrl : pageUrls) {
                futures.add(executor.submit(() -> scrapePage(pageUrl))); // Process each page in parallel
            }

            // Collect results from each thread
            for (Future<List<JobDto>> future : futures) {
                jobs.addAll(future.get()); // Get scraped jobs from each page
            }

            executor.shutdown(); // Shutdown executor

        } catch (Exception e) {
            throw new RuntimeException("Something went wrong", e);
        }
        return jobs;
    }

    /**
     * Scrapes job listings from a single page URL.
     */
    private List<JobDto> scrapePage(String pageUrl) {
        List<JobDto> jobList = new ArrayList<>();

        try {
            WebDriver driver = new ChromeDriver();
            driver.get(pageUrl);
            Thread.sleep(3000); // Wait for the page to load

            Document doc = Jsoup.parse(driver.getPageSource());
            Elements jobListings = doc.select(".srp-jobtuple-wrapper");

            for (Element job : jobListings) {
                String title = job.select(".row1 h2 a").text();
                String jobLink = job.select(".row1 h2 a").attr("href");
                String companyName = job.select(".row2 .comp-name").text();
                String experience = job.select(".row3 .exp-wrap span.expwdth").text();
                String location = job.select(".row3 .loc-wrap span.locWdth").text();

                JobDto jobObj = JobDto.builder()
                        .title(title)
                        .company(companyName)
                        .location(location)
                        .experience(experience)
                        .applicationLink(jobLink)
                        .source("Naukri")
                        .scrapedAt(LocalDateTime.now())
                        .build();

                jobList.add(jobObj);
                this.jobEntityRepo.save(this.adapterService.convertDtoToEntity(jobObj));
            }

            driver.quit(); // Close WebDriver after scraping

        } catch (Exception e) {
            throw new RuntimeException("Something went wrong" , e);
        }

        return jobList;
    }
}
