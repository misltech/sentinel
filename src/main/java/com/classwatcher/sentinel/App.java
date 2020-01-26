package com.classwatcher.sentinel;

import java.time.Duration;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class App 
{
	  public static final String ACCOUNT_SID = "AC6a6209eba6bddff1fd289ea625a9a142";
	  public static final String AUTH_TOKEN = "bcf28f5b1679be554eda5425819cbbd2";
	  public static String TO = "";
	  public static final String FROM = "+17856694799";
	  public static String scrapethis = "";
	  public static HashMap<String, String> hm; 
	  public static Instant startTime;
	  
	  
	  public static void main(String[] args) throws InterruptedException {
		  
		  welcome();
		  initialize();
		  validateInputs(args);
		  addPhoneNumber(args);
		  
		  //if inputs are valid. do these
		  String url = "https://banner.newpaltz.edu/pls/PROD/bwckzschd.p_dsp_search?p_term=" + args[1] + hm.get(args[0].toUpperCase());
		 
		  WebDriver testURL = new HtmlUnitDriver();
		  try {
			  testURL.get(url);
			  
			  if(testURL.getCurrentUrl().equalsIgnoreCase("https://www.newpaltz.edu/classes/")) {
				  System.out.println("ERROR: cannot find that schedule. Its possible that newpaltz hasnt released that schedule yet.");
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
				
				boolean found = false;
				for (WebElement option : allOptions)
				{
					String temp = option.getText().replaceAll("\\s+", "");
					if (temp.equalsIgnoreCase(getCourseTitle(args)))
					{
						option.click();
						found = true;
					}
				}
				if(!found) {
					System.out.println("ERROR: Subject not found. Your input was: " +getCourseTitle(args));
					System.exit(1);
				}
			} catch (Exception ex)
			{
				System.out.println("Subject not found: " + getCourseTitle(args));
				System.exit(1);
			}
		  
		  
		  System.out.println("Searching for " + getCourseTitle(args) + "...");
		  
		  WebElement submit = testURL.findElement(By.xpath("//button[@type='submit']"));
		  submit.click();
		  
		    scrapethis = testURL.getCurrentUrl();
		 	WebDriver driver = new HtmlUnitDriver();
			System.out.println("Searching this URL: " + scrapethis);
			System.out.println("For this CRN: " + getCourseCRN(args));
			try {
			driver.get(scrapethis);
			@SuppressWarnings("unused")
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
						System.out.println("\n" + d + " | This class is full! Trying again soon!\n");
						Thread.sleep(TimeUnit.MINUTES.toMillis(getDelay(args))); //5mins
					}
					else {
						System.out.println("Message ID: " + sendMessage(getCourseTitle(args) + " " + getCourseCRN(args) + " class available! \n" + "Total runtime was " + getDetailedRunTime(startTime, Instant.now()) + " to complete."));
						System.out.println(getCourseCRN(args) + " Class available!");
						System.out.println("Class sentinel ending...");
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
	
private static void addPhoneNumber(String[] args) {
	
		if(args[5].charAt(0) == 1 && args[5].length() == 11) {
			TO = "+" + args[5];
		}
	
	  else {
		  TO = "+1" + args[5];
	  }
	}

private static void validateInputs(String[] args) {
	Calendar d = new GregorianCalendar();
	if((args.length == 6)) { 
			  if(Integer.parseInt(args[4]) < 5) {
				  System.out.println("ERROR: CheckFrequency parse error. Input a frequency greater than or equal to 5mins.");
				  System.exit(1);
			  }
			  else if(args[5].length() < 10) {
				  System.out.println("ERROR: Phone number parse error. Phone number cant be less than 10 digits.");
				  System.exit(1);
			  }
		  }
		  
	  else if(args.length == 1 && args[0].equalsIgnoreCase("-help")) {
		  System.out.println("\t\tInput arguments are as follows: ");
		  System.out.println("Semester Year Single_Class_Subject Course_CRN Check_Frequency Phone_Number");
		  System.out.println("\t\t\tor");
		  System.out.println("Semester Year ClassSubjectPart1 ClassSubjectPart2 CourseCRN CheckFrequency PhoneNumber");
		  System.out.println("Example: Spring 2020 Computer Science 149 5 5555555555");
		  System.exit(2);
	  }
	  else if(args.length == 0) {
		  System.out.println("ERROR: Arguments required!");
		  System.out.println("Semester Year Single_Class_Subject Course_CRN Check_Frequency Phone_Number");
		  System.out.println("Example: Spring 2020 ComputerScience 149 5 5555555555");
		  System.exit(2);
	  }
	
	 if(hm.containsKey(args[0].toUpperCase())) {
		  if(Integer.parseInt(args[1]) > d.get(Calendar.YEAR) + 2) {
			  System.out.println("ERROR: Year parse error. Input year again.");
			  System.exit(1);   
		  }
	  }
	 
	 else {
		  System.out.println("ERROR: Semester parse error. Input Semester again.");
		  System.exit(1);  
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
		delay = Long.parseLong(d[4]);
	return delay;
	
}

public static String getCourseTitle(String[] a){
	String title = null;
	title = a[2];
	return title;
}
public static String getCourseCRN(String[] c) {
	String crn = null;
	crn = c[3];
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
	System.out.println(
			
			
			
			
			"THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR\nIMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,\nFITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE\nAUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER\nLIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,\nOUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE\nSOFTWARE."
			
			);
	System.out.println();
	
}
	  
public static void textString(String[] t) {
	System.out.println("\n ----Testing String----");
	for(String ef: t) {
		System.out.println(ef);
	}
	System.out.println(" -----End----- \n");
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
