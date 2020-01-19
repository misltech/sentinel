package com.classwatcher.sentinel;

import java.sql.Driver;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Scanner;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class App 
{
	public static final String ACCOUNT_SID = "AC6a6209eba6bddff1fd289ea625a9a142";
	  public static final String AUTH_TOKEN = "bcf28f5b1679be554eda5425819cbbd2";
	  public static String TO = "+13477969840";
	  public static final String FROM = "+17856694799";
	  public static final String scrapethis = "https://banner.newpaltz.edu/pls/PROD/bwckzschd.p_dsp_results?p_term=202001&p_subj=GLG&p_crses=&p_title=&p_credits=&p_days=&p_time_span=&p_levl=&p_instr_pidm=&p_attr=";
	  public static Instant start;
	  public static Instant end;
	  
	
	  
	  
	  public static void main(String[] args) throws InterruptedException {
		  Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
		  
		  HashMap hm = new HashMap();
		  hm.put("SPRING", "01");
		  hm.put("SUMMER", "09");
		  hm.put("WINTER", "06");
		  hm.put("FALL", "00");
		    
		  //Semester, year, ClassSubject, Course CRN, CheckFrequency, Phone number
		  
		  if(!(args.length == 6)) {
			  System.out.println("ERROR: Wrong arguments added. Run program again with correct arguments");
			  System.exit(1);
		  }
		  
		  String url = "https://banner.newpaltz.edu/pls/PROD/bwckzschd.p_dsp_search?p_term=" + args[1] +  hm.get(args[0].toUpperCase());
		 
		  WebDriver testURL = new HtmlUnitDriver();
		  testURL.get(url);
		  
		  if(testURL.getCurrentUrl().equalsIgnoreCase("https://www.newpaltz.edu/classes/")) {
			  System.out.println("Semester or Year is incorrect");
			  System.exit(2);
		  }
		  
		    System.setProperty("webdriver.gecko.driver","C:\\Users\\tahir\\eclipse-workspace\\watchclassv1\\src\\main\\java\\com\\watchclass\\watchclassv1\\geckodriver.exe");
			WebDriver driver = new HtmlUnitDriver();
			System.out.println("Starting watch class");
			driver.get(scrapethis);
			WebElement e = driver.findElement(By.cssSelector(".results-table > tbody:nth-child(1) > tr:nth-child(6) > td:nth-child(12) > a:nth-child(1) > abbr:nth-child(1)"));		
			
			start = java.time.Instant.now();
			while(true) {
				if(e.getText().equalsIgnoreCase("F")) {
					driver.get(scrapethis);
					e = driver.findElement(By.cssSelector(".results-table > tbody:nth-child(1) > tr:nth-child(6) > td:nth-child(12) > a:nth-child(1) > abbr:nth-child(1)"));		
					System.out.println("Taking a break ibb");
					Thread.sleep(300000); //5mins
				}
				else {
					
					sendMessage("Geology class available! \n" + "It took " + getRunTime(start, end) + " to complete.");
					
					System.out.println("1245 Class available");
					System.out.println("Ending watch class");
					end = java.time.Instant.now();
					break;
			}
		}
				
	  }
	

public static String sendMessage(String m) {
	Message message = Message.creator(new PhoneNumber(TO + ""),new PhoneNumber(FROM), m).create();

  return message.getSid();

}

public static String getRunTime(Instant start, Instant end) {
	Duration between = java.time.Duration.between(start, end);
	String out = between.toDays() + "\t" + between.toHours() + ":" + between.toMinutes() + ":"+ between.getSeconds();
	return out;
}
}
