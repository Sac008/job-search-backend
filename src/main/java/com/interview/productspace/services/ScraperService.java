package com.interview.productspace.services;

import com.interview.productspace.DTOs.JobDto;
import com.interview.productspace.Repository.JobEntityRepository;
import com.interview.productspace.ScrapeJobs.IScrapeJobs;
import com.interview.productspace.models.JobEntity;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ScraperService {

    @Autowired
    JobEntityRepository jobEntityRepository;
    private static final long REFRESH_INTERVAL_HOURS = 6;

    private static final String NAUKRI_URL = "https://www.naukri.com/%s-jobs";
    private static final String LINKEDIN_URL = "http://www.linkedin.com/jobs/search?keywords=%s";
    private static final String INDEED_URL = "http://www.indeed.com/jobs?q=%s";


    private List<IScrapeJobs> scrapeJobs = new ArrayList<>();

    @Autowired
    public ScraperService(List<IScrapeJobs> scrapeJobs) {
        this.scrapeJobs = scrapeJobs;
    }

    public List<JobDto> getJobListings(String jobTitle) {
        List<JobDto> jobList = new ArrayList<>();
        for(IScrapeJobs scrapeJob : scrapeJobs) {
            jobList.addAll(scrapeJob.scrapeJob(jobTitle));
        }
        return jobList;
    }



//    public List<JobEntity> scrapeAndStoreJobs(String jobTitle) {
//
////        LocalDateTime lastScrapedTime = jobEntityRepository.findLatestScrapedTime();
////
////        if(lastScrapedTime != null && lastScrapedTime.isAfter(LocalDateTime.now().minusHours(REFRESH_INTERVAL_HOURS))) {
////            return jobEntityRepository.findAll();
////        }
////        jobEntityRepository.deleteAll();
//
//        return scrapeNaukriJobs(jobTitle);
////        CompletableFuture<List<JobEntity>> linkedInJobs = scrapeLinkedInJobs(jobTitle);
////        CompletableFuture<List<JobEntity>> indeedJobs = scrapeIndeedJobs(jobTitle);
//
//
//
////        return CompletableFuture.allOf(naukriJobs)
////                .thenApply(ignored -> {
////                    List<JobEntity> allJobs = new ArrayList<>();
////                    allJobs.addAll(naukriJobs.join());
//////                    allJobs.addAll(linkedInJobs.join());
//////                    allJobs.addAll(indeedJobs.join());
////
////                    jobEntityRepository.saveAll(allJobs);
////                    return allJobs;
////                });
//    }
//
//    private List<JobEntity> scrapeNaukriJobs(String jobTitle) {
//            List<JobEntity> jobs = new ArrayList<>();
//            String formattedUrl = String.format(NAUKRI_URL, jobTitle.replace(" " , "-"));
//            try {
//                System.setProperty("webdriver.chrome.driver", "C:\\Users\\Sachin Singh\\Downloads\\chromedriver-win64\\chromedriver-win64\\chromedriver.exe");
//
//                // Initialize Chrome browser
//                WebDriver driver = new ChromeDriver();
//
//                driver.get(formattedUrl);
//
//                // Wait for JavaScript to load (Adjust if needed)
//                Thread.sleep(5000);
//
//                // Get full page HTML (after JavaScript execution)
//                String pageSource = driver.getPageSource();
//                Document doc = Jsoup.parse(pageSource);
//                // Select all job listings
//                Elements jobListings = doc.select(".srp-jobtuple-wrapper");
//
//                for (Element job : jobListings) {
//                    // Extract Job Title
//                    String title = job.select(".row1 h2 a").text();
//                    String jobLink = job.select(".row1 h2 a").attr("href");
//
//                    // Extract Company Name
//                    String companyName = job.select(".row2 .comp-name").text();
//
//                    // Extract Experience
//                    String experience = job.select(".row3 .exp-wrap span.expwdth").text();
//
//                    // Extract Salary
//                    String salary = job.select(".row3 .sal-wrap span").text();
//
//                    // Extract Location
//                    String location = job.select(".row3 .loc-wrap span.locWdth").text();
//
//                    // Extract Job Description
//                    String jobDesc = job.select(".row4 .job-desc").text();
//
//                    JobEntity jobObj = JobEntity.builder()
//                            .title(title)
//                            .company(companyName)
//                            .location(location)
//                            .experience(experience)
//                            .applicationLink(jobLink)
//                            .scrapedAt(LocalDateTime.now())
//                            .build();
//
//                    jobs.add(jobObj);
//
//                    // Print extracted details
//                    System.out.println("---------------------------------------------------");
//                    System.out.println("Job Title: " + title);
//                    System.out.println("Job Link: " + jobLink);
//                    System.out.println("Company Name: " + companyName);
//                    System.out.println("Experience Required: " + experience);
//                    System.out.println("Salary: " + salary);
//                    System.out.println("Location: " + location);
//                    System.out.println("Job Description: " + jobDesc);
//                    System.out.println("---------------------------------------------------");
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return jobs;
//    }

//    private CompletableFuture<List<JobEntity>> scrapeLinkedInJobs(String jobTitle) {
//        return CompletableFuture.supplyAsync(() -> {
//            List<JobEntity> jobs = new ArrayList<>();
//            String formattedUrl = String.format(LINKEDIN_URL, jobTitle.replace(" ", "%20"));
//            try {
//                Document doc = Jsoup.connect(formattedUrl).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36").get();
//                Elements jobElements = doc.select(".job-card-container");
//
//                for (Element jobElement : jobElements) {
//                    String title = jobElement.select(".job-card-list__title").text();
//                    String company = jobElement.select(".job-card-container__company-name").text();
//                    String location = jobElement.select(".job-card-container__metadata-item").text();
//                    String applicationLink = jobElement.select("a").attr("href");
//
//                    JobEntity job = JobEntity.builder()
//                            .title(title)
//                            .company(company)
//                            .location(location)
//                            .experience("NA")
//                            .applicationLink(applicationLink)
//                            .scrapedAt(LocalDateTime.now())
//                            .build();
//
//                    jobs.add(job);
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return jobs;
//        });
//    }

//    private CompletableFuture<List<JobEntity>> scrapeIndeedJobs(String jobTitle) {
//        return CompletableFuture.supplyAsync(() -> {
//            List<JobEntity> jobs = new ArrayList<>();
//            String formattedUrl = String.format(INDEED_URL, jobTitle.replace(" ", "+"));
//            try {
//                Document doc = Jsoup.connect(formattedUrl).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36").get();
//                Elements jobElements = doc.select(".job_seen_beacon");
//
//                for (Element jobElement : jobElements) {
//                    String title = jobElement.select(".jobTitle").text();
//                    String company = jobElement.select(".companyName").text();
//                    String location = jobElement.select(".companyLocation").text();
//                    String applicationLink = "https://www.indeed.com" + jobElement.select("a").attr("href");
//
//                    JobEntity job = JobEntity.builder()
//                            .title(title)
//                            .company(company)
//                            .location(location)
//                            .experience("NA")
//                            .applicationLink(applicationLink)
//                            .scrapedAt(LocalDateTime.now())
//                            .build();
//
//                    jobs.add(job);
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return jobs;
//        });
//    }

//    private org.w3c.dom.Document convertJsoupToW3C(Document jsoupDoc) throws Exception {
//        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//        DocumentBuilder builder = factory.newDocumentBuilder();
//        InputSource inputSource = new InputSource(new StringReader(jsoupDoc.html()));
//        return builder.parse(inputSource);
//    }

//    private String extractXPathValue(XPath xPath, Node node, String expression) {
//        try {
//            XPathExpression expr = xPath.compile(expression);
//            return expr.evaluate(node).trim();
//        } catch (Exception e) {
//            return "";
//        }
//    }
}
