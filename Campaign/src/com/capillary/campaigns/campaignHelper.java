/**
 * 
 */
/**
 * @author Pritish
 *
 */
package com.capillary.campaigns;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class campaignHelper	{
	
	//Initializations
	public static WebDriver driver;
	public static Log log = LogFactory.getLog(campaignHelper.class);
	public static final String DIAGNOSTICS_DEST_PROPERTY = "STDERR";
	
	public static void click(By by) {
		// wait until loading image is removed from the page and then start clicking on the required element
		sleep(3000);
		driver.findElement(by).click();
	}
	
	public static void enterInput(By by, String data) {
		sleep(2500);
		WebElement textBox = driver.findElement(by);
		if (!textBox.getAttribute("type").equals("file")) {
			textBox.clear();
		}
		textBox.sendKeys(data);
	}
	
	public static void selectItemFromListBox(By by, String item) {
		Select select = new Select(driver.findElement(by));
		select.selectByVisibleText(item);

		WebElement selectedOption = null;
		for (WebElement option : driver.findElement(by).findElements(By.tagName("option"))) {
			if (option.getText().equals(item)) {
				selectedOption = option;
				break;
			}
		}
	}
	
	public static List<WebElement> findElements(By by) {
		return driver.findElements(by);
	}
	
	public static void login(String username, String password)	{
		log.info("Attempting to login using username - " + username);
		enterInput(By.id("login__username"), username);
		enterInput(By.id("login__password"), password);
		click(By.id("c-login-btn"));
		log.info("Successfully logged in");
	}
	
	public static void selectOrg(String OrgName, String OrgId){
		log.info("Selecting the org - " + OrgName);
		click(By.id("org_selection_dropdown"));
		enterInput(By.id("org-search-div"), OrgName);
		click(By.id(OrgId));
	}
	
	public static void selectCampaignDate(String Date) throws Exception{	
		String[] dateComponents = Date.split(" ");
		assertForWebElementVisible(By.className("ui-datepicker-month"));
		Select MonthDropDown = new Select(driver.findElement(By.className("ui-datepicker-month")));
		Thread.sleep(2000);
		MonthDropDown.selectByVisibleText(dateComponents[1]);
		assertForWebElementVisible(By.className("ui-datepicker-year"));
		Select YearDropDown = new Select(driver.findElement(By.className("ui-datepicker-year")));
		Thread.sleep(2000);
		YearDropDown.selectByVisibleText(dateComponents[2]);
		WebElement dayElement = driver.findElement(By.xpath("//div[@id='ui-datepicker-div']/table//a[text()='" + dateComponents[0] + "']"));
		dayElement.findElement(By.xpath("..")).click();
	}

	/**
	 * sleeps until element is visible
	 */
	public static void assertForWebElementVisible(By by) {
		boolean result = false;
		WebElement webElement = null;
		long end = System.currentTimeMillis() + 300000;
		while (System.currentTimeMillis() < end) {
			try {
//				driver.switchTo().window(driver.getWindowHandle());
//				((JavascriptExecutor) driver).executeScript("window.focus();");
				webElement = driver.findElement(by);
				result = webElement.isDisplayed();
			} catch (NoSuchElementException e) {
				System.out.println("Coninuing");
				continue;
			}
			if (result == true) {
				return;
			}
		}
	}
	
	public static void sleep(int milliSecs) {
		try {
			Thread.sleep(milliSecs);
		} catch (InterruptedException e) {
			System.out.println(e.getMessage());
		}
	}
	
	public static void createCampaign(String campaignName, String startDate, String endDate) throws Exception {
		log.info("Creating a campaign");
		click(By.id("campaign"));
		click(By.id("new-campaign"));
		enterInput(By.id("campaign_name"), campaignName);
		log.info("Entering an end date for the campaign");
		click(By.id("cnew_end_date"));
		selectCampaignDate(endDate);
		log.info("Entering a start date for the campaign");
		click(By.id("cnew_start_date"));
		selectCampaignDate(startDate);
		click(By.id("CreateNewCampaign"));
		log.info("Completing creating the campaign - " + campaignName);
	}
	
	public static String createCouponSeriesAndReturnId(
			String campaignName,
			String couponSeriesTag,
			String couponDiscountOn,
			String couponDiscountValue,
			String couponDiscountType)	{
		//Since there are live, lapsed and upcoming campaigns we need to iterate to find the right type
		try	{
			click(By.linkText(campaignName));
		}
		catch (Exception e)	{
			click(By.xpath("//*[@id=\"c-type-status\"]/div[2]/a[2]"));
			try	{
				click(By.linkText(campaignName));
			}
			catch (Exception e1){
				click(By.xpath("//*[@id=\"c-type-status\"]/div[2]/a[3]"));
				try	{
					click(By.linkText(campaignName));
				}
				catch (Exception e2)	{
					log.error("Cannot click on created campaign - " + campaignName);
				}
			}
		}
		log.info("Creating coupon series");
		click(By.id("newCoupon"));
		enterInput(By.id("info"), couponSeriesTag);
		Select couponOfferOn = new Select(driver.findElement(By.id("discount_on")));
		couponOfferOn.selectByVisibleText(couponDiscountOn.toUpperCase());
		enterInput(By.id("discount_value"), couponDiscountValue);
		Select cpnDiscountType = new Select(driver.findElement(By.id("discount_type")));
		cpnDiscountType.selectByVisibleText(couponDiscountType);
		click(By.id("createNewSeries"));
		log.info("New coupon series with name - " + couponSeriesTag + " created.");
		String[] URL = driver.getCurrentUrl().split("\\/");
		String id = URL[URL.length - 1];
		return id;
	}
	
	public static void main(String[] args) throws Exception	{
		/*
		 * The order in which the arguments will be passed in will be as follows : 
		 *  - URL
		 *  - username
		 *  - password
		 *  - org name
		 *  - org id
		 *  - campaign name
		 *  - campaign start date
		 *  - campaign end date
		 *  - coupon series.
		 *  - couponDiscountOn
		 *  - couponDiscountValue
		 *  - couponDiscountType
		 */
		try	{
//			System.setProperty("webdriver.chrome.driver", "C:\\drivers\\chromedriver.exe");
			String driverLocation = campaignHelper.class.getProtectionDomain().getCodeSource().getLocation().getPath().replace("campaignHelper.jar", "");
			System.out.println("Driver Location - " + driverLocation);
			System.setProperty("webdriver.chrome.driver", driverLocation + "/chromedriver.exe");
			driver = new ChromeDriver();
			driver.manage().window().maximize();
			driver.get(args[0]);
			login(args[1], args[2]);
			selectOrg(args[3], args[4]);
			createCampaign(args[5], args[6], args[7]);
			String id = createCouponSeriesAndReturnId(args[5], args[8], args[9], args[10], args[11]);
			log.info("SUCCESS : " + id);
			driver.close();
			System.exit(0);
		}
		catch (Exception e){
			log.error("Unable to continue - error message - " + e);
			log.error("FAILURE", e);
			driver.close();
			System.exit(1);
		}
	}

}