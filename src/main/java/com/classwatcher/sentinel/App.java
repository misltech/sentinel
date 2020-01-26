package com.classwatcher.sentinel;

import java.net.SocketException;
import java.sql.Driver;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.gargoylesoftware.htmlunit.SilentCssErrorHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class App 
{
	  public static final String ACCOUNT_SID = "AC6a6209eba6bddff1fd289ea625a9a142";
	  public static final String AUTH_TOKEN = "bcf28f5b1679be554eda5425819cbbd2";
	  public static String TO = "+13477969840";
	  public static final String FROM = "+17856694799";
	  public static String scrapethis = "https://banner.newpaltz.edu/pls/PROD/bwckzschd.p_dsp_results?p_term=202001&p_subj=GLG&p_crses=&p_title=&p_credits=&p_days=&p_time_span=&p_levl=&p_instr_pidm=&p_attr=";
	  public static HashMap<String, String> hm; 
	  public static Instant startTime;
	  
	  
	  public static void main(String[] args) throws InterruptedException {
		  
		  welcome();
		  initialize();
		  
		    
		  //Semester, year, ClassSubject, CourseCRN, CheckFrequency, PhoneNumber = 6
		  //or 
		  //Semester, year, Class, ClassSubject2, CourseCRN, CheckFrequency, PhoneNumber  = 7
		  //check input is valid here.
		  if((args.length == 6) || args.length == 7) {
			  
		  }
		  else {
			  System.out.println("ERROR: Wrong arguments added. Run program again with correct arguments");
			  System.exit(1);
		  }
		  
		  //if inputs are valid. do these
		  String url = "https://banner.newpaltz.edu/pls/PROD/bwckzschd.p_dsp_search?p_term=" + args[1] +  hm.get(args[0].toUpperCase());
		 
		  WebDriver testURL = new HtmlUnitDriver();
		  try {
			  testURL.get(url);
			  
			  if(testURL.getCurrentUrl().equalsIgnoreCase("https://www.newpaltz.edu/classes/")) {
				  System.out.println("Semester and/or year is incorrect");
				  System.exit(2);
			  }
		  }
		  catch(Exception e) {
			  System.out.println(e);
		  }
		  
		  try
			{
				WebElement select = testURL.findElement(By.xpath("//select[@id='p_subj']"));
				List<WebElement> allOptions = select.findElements(By.tagName("option"));
				for (WebElement option : allOptions)
				{
					if (option.getText().equals(getCourseTitle(args)))
					{
						option.click();
					}
				}
			} catch (Exception ex)
			{
				System.out.println("Error clicking option " + getCourseTitle(args));
			}
		  
		  System.out.println("Found Subject...");
		  
		  WebElement submit = testURL.findElement(By.xpath("//button[@type='submit']"));
		  submit.click();
		  
		    scrapethis = testURL.getCurrentUrl();
		 	WebDriver driver = new HtmlUnitDriver();
			System.out.println("Looking for your class..");
			
			System.out.println("Searching this URL: " + scrapethis);
			System.out.println("Using this CRN: " + getCourseCRN(args));
			try {
			System.out.println("Testing it against the website!");
			driver.get(scrapethis);
			WebElement e = driver.findElement(By.xpath("//*[@data-crn='" + getCourseCRN(args) + "']"));
			System.out.println("Test Case Complete \n\n");
			
			}
			catch (Exception exp) {
				System.out.println("CRN not found!");
				System.exit(3);
			}
			
			
			while(allowRuntime()) { 

				try {
					WebDriver continousDriver = new HtmlUnitDriver();
					continousDriver.get(scrapethis);
					WebElement e = continousDriver.findElement(By.xpath("//*[@data-crn='" + getCourseCRN(args) + "']"));
					System.out.println("Searching this CRN: " + getCourseCRN(args));
					String[] rowText = e.getText().split("\\s+");
					String seats = rowText[rowText.length - 1];
					System.out.println("Number of seats: " + seats);
						
					if(seats.equals("F")) {
						Date d = new Date();
						System.out.println("\n" + d + " Class is still full! Trying again soon!\n");
						Thread.sleep(TimeUnit.MINUTES.toMillis(getDelay(args))); //5mins
					}
					else {
						sendMessage(getCourseTitle(args) + " " + getCourseCRN(args) + " class available! \n" + "Total runtime was " + getDetailedRunTime(startTime, Instant.now()) + " to complete.");
						System.out.println(getCourseCRN(args) + " Class available!");
						System.out.println("Ending watch class...");
						break;
					}
					
				}
				catch(NoSuchElementException nse) {
					System.out.println(nse);
					throw nse;
				}
				catch(WebDriverException se) {
					Thread.sleep(300000);
					throw se;
				}
					
		}
			
	  }
	
private static void initialize() {
	hm = new HashMap<String, String>();
	hm.put("SPRING", "01");
	hm.put("SUMMER", "09");
	hm.put("WINTER", "06");
	hm.put("FALL", "00");
	
	Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
	
	startTime = Instant.now();
	Logger logger = Logger.getLogger("");
	logger.setLevel(Level.OFF);
      
	}


public static boolean allowRuntime() {
	
	if(getRunTime(startTime, Instant.now()) > 7) {
		return false;
	}
	
	return true;
	
}

public static long getDelay(String[] d) {
	long delay = 0;
	if(d.length == 6) {
		delay = Long.parseLong(d[4]);
	}
	else if (d.length == 7) {
		delay = Long.parseLong(d[5]);
	}
	return delay;
	
}

public static String getCourseTitle(String[] a){
	String title = null;
	if(a.length == 6) {
		title = a[2];
	}
	else if (a.length == 7) {
		title = a[2] + " " + a[3];
	}
	return title;

}
public static String getCourseCRN(String[] c) {
	String crn = null;
	if(c.length == 6) {
		crn = c[3];
	}
	else if(c.length == 7) {
		crn = c[4];
	}
	return crn;
}
public static void welcome() {
	System.out.println("\r\n" + 
			" _______  _        _______  _______  _______    _______  _______  _       __________________ _        _______  _       \r\n" + 
			"(  ____ \\( \\      (  ___  )(  ____ \\(  ____ \\  (  ____ \\(  ____ \\( (    /|\\__   __/\\__   __/( (    /|(  ____ \\( \\      \r\n" + 
			"| (    \\/| (      | (   ) || (    \\/| (    \\/  | (    \\/| (    \\/|  \\  ( |   ) (      ) (   |  \\  ( || (    \\/| (      \r\n" + 
			"| |      | |      | (___) || (_____ | (_____   | (_____ | (__    |   \\ | |   | |      | |   |   \\ | || (__    | |      \r\n" + 
			"| |      | |      |  ___  |(_____  )(_____  )  (_____  )|  __)   | (\\ \\) |   | |      | |   | (\\ \\) ||  __)   | |      \r\n" + 
			"| |      | |      | (   ) |      ) |      ) |        ) || (      | | \\   |   | |      | |   | | \\   || (      | |      \r\n" + 
			"| (____/\\| (____/\\| )   ( |/\\____) |/\\____) |  /\\____) || (____/\\| )  \\  |   | |   ___) (___| )  \\  || (____/\\| (____/\\\r\n" + 
			"(_______/(_______/|/     \\|\\_______)\\_______)  \\_______)(_______/|/    )_)   )_(   \\_______/|/    )_)(_______/(_______/\r\n" + 
			"                                                                                                                       \r\n" + 
			"");
	
}
	  
public static void textString(String[] t) {
	System.out.println("\n ----Testing String----");
	for(String ef: t) {
		System.out.println(ef);
	}
	System.out.println(" ----End----- \n");
}

public static String sendMessage(String m) {
	Message message = Message.creator(new PhoneNumber(TO + ""),new PhoneNumber(FROM), m).create();

  return message.getSid();

}



public static String getDetailedRunTime(Instant start, Instant end) {
	Duration between = java.time.Duration.between(start, end);
	String out = between.toDays() + "days " + between.toHours() + "hrs " + between.toMinutes() + "mins "+ between.getSeconds() + "secs";
	return out;
}

public static long getRunTime(Instant start, Instant end) {
	Duration between = java.time.Duration.between(start, end);
	long out = between.toDays();
	return out;
}
}
