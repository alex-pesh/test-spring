package com.example.test.integration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testcontainers.containers.BrowserWebDriverContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;


@Testcontainers
public class SeleniumTest {

	@Container
	static BrowserWebDriverContainer<?> chromeContainer = new BrowserWebDriverContainer<>().withCapabilities(new ChromeOptions());

	static RemoteWebDriver driver;


	@BeforeAll
	static void beforeAll() {
		driver = new RemoteWebDriver(chromeContainer.getSeleniumAddress(), new ChromeOptions());
	}

	@AfterAll
	static void afterAll() {
		driver.quit();
	}


	@Test
	void testViewHomePage() {
		String baseUrl = "https://www.vpnjantit.com/create-free-account?server=bg2&type=OpenVPN";
		driver.get(baseUrl);

		WebElement form = driver.findElement(By.xpath("//form"));
		form.findElement(By.name("user")).sendKeys("alex011");
		form.findElement(By.name("pass")).sendKeys("Alpha98765432");

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofMinutes(1));
//		wait.until(ExpectedConditions
//				.frameToBeAvailableAndSwitchToIt(By.partialLinkText("https://challenges.cloudflare.com"))
//						.andThen(ExpectedConditions.elementToBeClickable(By.cssSelector("input[type='checkbox'"))))
//				.findElement(By.cssSelector("input[type='checkbox'")).click();

//		driver.findElement(By.xpath("//div[@class='cf-turnstile']/div[1]")).getShadowRoot()

		driver.switchTo().defaultContent();
		form.submit();

//		driver.findElement(By.cssSelector("input[type='submit'")).click();
//		JavascriptExecutor executor = (JavascriptExecutor) driver;
//		executor.executeScript("arguments[0].scrollIntoView(true);", element1);

		assertThat(driver.getTitle()).isEqualTo("App Title");
	}

}
