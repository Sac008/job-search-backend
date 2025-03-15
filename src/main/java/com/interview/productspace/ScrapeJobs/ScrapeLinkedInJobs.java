package com.interview.productspace.ScrapeJobs;

import com.interview.productspace.DTOs.JobDto;
import com.interview.productspace.Repository.JobEntityRepository;
import com.interview.productspace.adapters.AdapaterService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

//@Service
public class ScrapeLinkedInJobs implements IScrapeJobs{
//    @Autowired
    private JobEntityRepository jobEntityRepo;

//    @Autowired
    private AdapaterService adapterService;
    private static final String LINKEDIN_URL = "http://www.linkedin.com/jobs/search?keywords=%s";


    @Override
    public List<JobDto> scrapeJob(String jobTitle) {
        List<JobDto> jobs = Collections.synchronizedList(new ArrayList<>()); // Thread-safe list
        String baseUrl = "https://www.linkedin.com"; // Ensure it's the correct base URL
        String formattedUrl = String.format(LINKEDIN_URL, jobTitle.replace(" ", "-"));

        try {

            WebDriver driver = new ChromeDriver();
            driver.get(formattedUrl);

            driver.manage().deleteAllCookies(); // Clear existing cookies

            // Example: Manually add a few cookies (Replace with actual values)
            driver.manage().addCookie(new Cookie("li_at", ""));
            driver.manage().addCookie(new Cookie("JSESSIONID", ""));

            // Wait for the page to load
            Thread.sleep(3000);

            // Parse the first page
            Document doc = Jsoup.parse(driver.getPageSource());
            Elements paginationElements = doc.select(".artdeco-pagination__indicator button");

            int lastPage = 1;
            for (Element page : paginationElements) {
                String pageText = page.text();
                if (pageText.matches("\\d+")) {
                    lastPage = Math.max(lastPage, Integer.parseInt(pageText));
                }
            }
            driver.quit();

            // Multi-threaded scraping
            ExecutorService executor = Executors.newFixedThreadPool(5);
            List<Future<List<JobDto>>> futures = new ArrayList<>();

            for (int page = 1; page <= lastPage; page++) {
                String pageUrl = formattedUrl + "&start=" + ((page - 1) * 25); // LinkedIn uses start= for pagination
                futures.add(executor.submit(() -> scrapePage(pageUrl)));
            }

            for (Future<List<JobDto>> future : futures) {
                jobs.addAll(future.get());
            }

            executor.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jobs;
    }

    /**
     * Scrapes job listings from a single page URL.
     */
    private List<JobDto> scrapePage(String pageUrl) {
        List<JobDto> jobList = new ArrayList<>();

        try {
//            ChromeOptions options = new ChromeOptions();
//            options.addArguments("--headless"); // Run in headless mode
            WebDriver driver = new ChromeDriver();
            driver.get(pageUrl);
            Thread.sleep(3000); // Wait for the page to load

            Document doc = Jsoup.parse(driver.getPageSource());
            Elements jobListings = doc.select(".artdeco-entity-lockup__content");

            for (Element job : jobListings) {
                String title = job.select(".job-card-container__link").text();
                String jobLink = "https://www.linkedin.com" + job.select(".job-card-container__link").attr("href");
                String companyName = job.select(".artdeco-entity-lockup__subtitle").text();
                String location = job.select(".artdeco-entity-lockup__caption li span").text();
                String experience = ""; // Experience is not available in the given HTML

                JobDto jobObj = JobDto.builder()
                        .title(title)
                        .company(companyName)
                        .location(location)
                        .experience(experience)
                        .applicationLink(jobLink)
                        .source("LinkedIn")
                        .scrapedAt(LocalDateTime.now())
                        .build();

                jobList.add(jobObj);
                this.jobEntityRepo.save(this.adapterService.convertDtoToEntity(jobObj));
            }

            driver.quit(); // Close WebDriver after scraping

        } catch (Exception e) {
            e.printStackTrace();
        }

        return jobList;
    }
}
