package com.interview.productspace.Configuration;

import jakarta.annotation.PostConstruct;
import org.openqa.selenium.WebDriver;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChromeDriverConfiguration {

    @PostConstruct
    public void getChromeDriver() {
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\Sachin Singh\\Downloads\\chromedriver-win64\\chromedriver-win64\\chromedriver.exe");
    }
}
