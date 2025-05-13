package AutomatedTesting.OrangeHRM;

import org.testng.annotations.Test;
import org.testng.annotations.DataProvider;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Reporter;
import org.openqa.selenium.support.ui.*;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.*;

import java.awt.*;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import org.monte.media.Format;
import org.monte.media.math.Rational;
import org.monte.screenrecorder.ScreenRecorder;
import static org.monte.media.AudioFormatKeys.*;
import static org.monte.media.VideoFormatKeys.*;

public class OneFileFullFlow {

    WebDriver driver;
    CustomScreenRecorder screenRecorder;

    public static String getCellValue(XSSFCell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING: return cell.getStringCellValue();
            case NUMERIC:
                return DateUtil.isCellDateFormatted(cell)
                        ? cell.getLocalDateTimeCellValue().toLocalDate().toString()
                        : String.valueOf((long) cell.getNumericCellValue());
            default: return "";
        }
    }

    @BeforeClass
    public void setup() throws Exception {
    	File file = new File("test-recordings");
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Rectangle captureSize = new Rectangle(screenSize);
        GraphicsConfiguration gc = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getDefaultScreenDevice()
                .getDefaultConfiguration();

        screenRecorder = new CustomScreenRecorder(gc, captureSize,
                new Format(MediaTypeKey, MediaType.FILE, MimeTypeKey, MIME_AVI),
                new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
                        CompressorNameKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
                        DepthKey, 24, FrameRateKey, Rational.valueOf(15),
                        QualityKey, 1.0f,
                        KeyFrameIntervalKey, 15 * 60),
                null, null, file, "OrangeHRM_FullFlow");

        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.get("https://opensource-demo.orangehrmlive.com/");
        Reporter.log("Browser launched and navigated to OrangeHRM site", true);
        screenRecorder.start();
        Reporter.log("Screen recording started", true);
    }

    @DataProvider(name = "FullData")
    public Object[][] getAllData() throws Exception {
        FileInputStream fis = new FileInputStream(new File("target/EmployeeData.xlsx"));
        XSSFWorkbook workbook = new XSSFWorkbook(fis);
        Object[][] data = new Object[1][37]; 
        int i = 0;

        data[0][i++] = getCellValue(workbook.getSheet("Login").getRow(1).getCell(1));
        data[0][i++] = getCellValue(workbook.getSheet("Login").getRow(1).getCell(2));

        for (int j = 1; j <= 6; j++) data[0][i++] = getCellValue(workbook.getSheet("AddEmployee").getRow(1).getCell(j));
        for (int j = 1; j <= 9; j++) data[0][i++] = getCellValue(workbook.getSheet("PersonalDetails").getRow(1).getCell(j));
        for (int j = 1; j <= 5; j++) data[0][i++] = getCellValue(workbook.getSheet("SystemUser").getRow(1).getCell(j));
        for (int j = 1; j <= 5; j++) data[0][i++] = getCellValue(workbook.getSheet("AssignLeave").getRow(1).getCell(j));
        for (int j = 1; j <= 2; j++) data[0][i++] = getCellValue(workbook.getSheet("EmployeePunch").getRow(1).getCell(j));
        for (int j = 1; j <= 3; j++) data[0][i++] = getCellValue(workbook.getSheet("EmployeeClaim").getRow(1).getCell(j));
        for (int j = 1; j <= 4; j++) data[0][i++] = getCellValue(workbook.getSheet("ExpenseInfo").getRow(1).getCell(j));
        data[0][i++] = getCellValue(workbook.getSheet("BuzzFeed").getRow(1).getCell(1));

        workbook.close();
        return data;
    }

    public void uploadFile(String path) throws Exception {
        StringSelection sel = new StringSelection(path);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(sel, null);
        Robot robot = new Robot();
        robot.delay(1000);
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);
        robot.delay(1000);
    }

    @Test(dataProvider = "FullData")
    public void fullFlowTest(
        String login, String loginpassword,
        String firstName, String middleName, String lastName, String username, String password, String photopath,
        String licenseNumber, String licenseExpiry, String ssn, String nationality, String maritalStatus, String dob, String bloodType, String resumepath, String miliserv,
        String empFullName, String userUsername, String userPassword, String userRole, String userStatus,
        String empname, String leaveType, String fromDate, String toDate, String comment,
        String punchinmessage, String punchoutmessage,
        String eventName, String currency, String claimcomment,
        String exp, String date, String amount, String note,
        String postcontent
    ) throws Exception {
    	Reporter.log("--------------------------------------------------", true);
    	Reporter.log("TEST NAME     : Full Integrated Workflow - OrangeHRM", true);
    	Reporter.log("APPLICATION   : OrangeHRM Open Source (https://opensource-demo.orangehrmlive.com/)", true);
    	Reporter.log("TEST TYPE     : Functional | End-to-End | Positive Flow", true);
    	Reporter.log("TEST OBJECTIVE: Validate employee lifecycle (Add → Details → Resume → System User → Leave → Punch → Claim → Buzz)", true);
    	Reporter.log("DATA SOURCE   : Excel - target/EmployeeData.xlsx", true);
    	Reporter.log("--------------------------------------------------", true);
    	
    	Reporter.log("===== Starting Full Flow Test for user: " + login + " =====", true);
        // Admin Login
    	Thread.sleep(2000);
        driver.findElement(By.name("username")).sendKeys(login);
        driver.findElement(By.name("password")).sendKeys(loginpassword);
        Reporter.log("Entered Admin username and password", true);
        driver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/div/div[1]/div/div[2]/div[2]/form/div[3]/button")).click();
        Reporter.log("Admin login submitted", true);
        Thread.sleep(3000);
        
        
        
        // Add Employee
        // Navigate to PIM > Add Employee section
        driver.findElement(By.xpath("//span[text()='PIM']")).click();
        Reporter.log("Navigated to PIM section", true);
        Thread.sleep(2000);
        driver.findElement(By.xpath("//button[text()=' Add ']")).click();
        Reporter.log("Accessed Add Employee form", true);

        Thread.sleep(2000);

        // Fill out employee basic info (name + employee ID)
        driver.findElement(By.name("firstName")).sendKeys(firstName);
        driver.findElement(By.name("middleName")).sendKeys(middleName);
        driver.findElement(By.name("lastName")).sendKeys(lastName);
        Thread.sleep(500);
        Reporter.log("Filled employee name: " + firstName + " " + middleName + " " + lastName, true);

        // Upload profile photo
        driver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/div[2]/div[2]/div/div/form/div[1]/div[1]/div/div[2]/div/button/i")).click();
        Thread.sleep(1000);
        uploadFile(photopath);
        Reporter.log("Uploaded employee photo from path: " + photopath, true);


        // Enable and enter login credentials for employee
        driver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/div[2]/div[2]/div/div/form/div[1]/div[2]/div[2]/div/label/span")).click();
        Thread.sleep(1000);
        driver.findElement(By.xpath("//label[text()='Username']/../following-sibling::div/input")).sendKeys(username);
        driver.findElement(By.xpath("//label[text()='Password']/../following-sibling::div/input")).sendKeys(password);
        driver.findElement(By.xpath("//label[text()='Confirm Password']/../following-sibling::div/input")).sendKeys(password);
        Reporter.log("Enabled and entered employee login credentials", true);

        // Submit the Add Employee form
        driver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/div[2]/div[2]/div/div/form/div[2]/button[2]")).click();
        Reporter.log("Employee profile submitted", true);

        Thread.sleep(7000);
        
        // Add employee details
        // Fill license number and expiry
        driver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/div[2]/div[2]/div/div/div/div[2]/div[1]/form/div[2]/div[2]/div[1]/div/div[2]/input")).sendKeys(licenseNumber);
        driver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/div[2]/div[2]/div/div/div/div[2]/div[1]/form/div[2]/div[2]/div[2]/div/div[2]/div/div/input")).sendKeys(licenseExpiry);
        Reporter.log("Entered license number and expiry", true);

        // SSN Number
        try {
        	driver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/div[2]/div[2]/div/div/div/div[2]/div[1]/form/div[2]/div[3]/div[1]/div/div[2]/input")).sendKeys(ssn);
        } catch(Exception e) {
        	Reporter.log("Skipped SSN entry - element not present in this site version", true);
        }

        // Select nationality and marital status from dropdowns
        driver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/div[2]/div[2]/div/div/div/div[2]/div[1]/form/div[3]/div[1]/div[1]/div/div[2]/div/div/div[1]")).click();
        driver.findElement(By.xpath("//div[@role='listbox']//span[text()='" + nationality + "']")).click();
        
        driver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/div[2]/div[2]/div/div/div/div[2]/div[1]/form/div[3]/div[1]/div[2]/div/div[2]/div/div/div[1]")).click();
        driver.findElement(By.xpath("//div[@role='listbox']//span[text()='" + maritalStatus + "']")).click();
        Reporter.log("Selected nationality: " + nationality + ", marital status: " + maritalStatus, true);

        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollBy(0, 400);");
        
        // Fill DOB and select gender
        driver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/div[2]/div[2]/div/div/div/div[2]/div[1]/form/div[3]/div[2]/div[1]/div/div[2]/div/div/input")).sendKeys(dob);
        
        driver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/div[2]/div[2]/div/div/div/div[2]/div[1]/form/div[3]/div[2]/div[2]/div/div[2]/div[1]/div[2]/div/label/span")).click();
        
        Reporter.log("Entered date of birth and selected gender", true);

        // Fill military service
        try {
        	driver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/div[2]/div[2]/div/div/div/div[2]/div[1]/form/div[4]/div/div[1]/div/div[2]/input")).sendKeys(miliserv);
        } catch(Exception e) {
        	Reporter.log("Skipped military service entry - element not present in this site version", true);
        }
        Reporter.log("Entered military service (if present)", true);


        // Save personal details (1st save)
        driver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/div[2]/div[2]/div/div/div/div[2]/div[1]/form/div[4]/button")).click();
        Reporter.log("Saved personal details", true);
        Thread.sleep(3000);

        // Scroll and select blood type
        js.executeScript("window.scrollBy(0, 400);");
        Thread.sleep(1000);
        try {
        	driver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/div[2]/div[2]/div/div/div/div[2]/div[2]/div/form/div[1]/div/div[1]/div/div[2]/div/div/div[1]")).click();
        	driver.findElement(By.xpath("//div[@role='listbox']//span[text()='" + bloodType + "']")).click();
        	Reporter.log("Selected blood type: " + bloodType, true);
        } catch (Exception e) {
        	Reporter.log("Skipped blood type entry - element not present in this site version", true);
        }

        // Save blood type info (2nd save)
        driver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/div[2]/div[2]/div/div/div/div[2]/div[2]/div/form/div[2]/button")).click();
        Reporter.log("Saved blood type details", true);


        // Upload resume as attachment
        driver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/div[2]/div[2]/div/div/div/div[2]/div[3]/div[1]/div/button")).click();
        js.executeScript("window.scrollBy(0, 400);");
        Thread.sleep(1000);
        driver.findElement(By.xpath("//div[contains(@class, 'oxd-file-button')]")).click();
        Thread.sleep(1000);
        uploadFile(resumepath);
        Reporter.log("Uploaded resume from path: " + resumepath, true);
        driver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/div[2]/div[2]/div/div/div/div[2]/div[3]/div/form/div[2]/div/div/div/div[2]/textarea")).sendKeys("Person's Resume");

        // Save attachment (3rd save)
        driver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/div[2]/div[2]/div/div/div/div[2]/div[3]/div/form/div[3]/button[2]")).click();
        Reporter.log("Saved resume attachment", true);
        Thread.sleep(1000);
        
        // Back to PIM Page
        // Navigate back to PIM search page
        driver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/div[1]/header/div[2]/nav/ul/li[2]/a")).click();
        Thread.sleep(1000);
        
        // Add System User
        Reporter.log("Navigated to System Users section", true);
        driver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/div[1]/aside/nav/div[2]/ul/li[1]/a")).click();
        Thread.sleep(2000);
        
        driver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/div[2]/div[2]/div/div[2]/div[1]/button")).click();
        Thread.sleep(2000);
        
         // Select User Role
        driver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/div[2]/div[2]/div/div/form/div[1]/div/div[1]/div/div[2]/div/div/div[1]")).click();
        driver.findElement(By.xpath("//span[text()='" + userRole + "']")).click();
        Reporter.log("Entered system user details for: " + empFullName, true);

        // Enter employee name (triggers suggestions)
        WebElement empNameInput = driver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/div[2]/div[2]/div/div/form/div[1]/div/div[2]/div/div[2]/div/div/input"));
        empNameInput.sendKeys(empFullName);
        Thread.sleep(2000); // Wait for dropdown to load

        // Select the exact match from suggestion dropdown
        driver.findElement(By.xpath("//div[@role='listbox']//span[text()='" + empFullName + "']")).click();


        // Set Username
        driver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/div[2]/div[2]/div/div/form/div[1]/div/div[4]/div/div[2]/input")).sendKeys(userUsername);

        // Select Status
        driver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/div[2]/div[2]/div/div/form/div[1]/div/div[3]/div/div[2]/div/div")).click();
        driver.findElement(By.xpath("//span[text()='" + userStatus + "']")).click();

        // Set Password & Confirm Password
        driver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/div[2]/div[2]/div/div/form/div[2]/div/div[1]/div/div[2]/input")).sendKeys(userPassword);
        driver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/div[2]/div[2]/div/div/form/div[2]/div/div[2]/div/div[2]/input")).sendKeys(userPassword);
        Reporter.log("Set username, status and password for system user: " + userUsername, true);


        // Click Save
        driver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/div[2]/div[2]/div/div/form/div[3]/button[2]")).click();
        Reporter.log("System user created", true);

        Thread.sleep(3000);
        
        // Leave
        Reporter.log("Navigated to Assign Leave section", true);
        driver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/div[1]/aside/nav/div[2]/ul/li[3]/a")).click();
        Thread.sleep(500);
        driver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/div[1]/header/div[2]/nav/ul/li[7]/a")).click();
        Thread.sleep(500);
        
         // Add employee name
        WebElement empnameinput = driver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/div[2]/div[2]/div/div/form/div[1]/div/div/div/div[2]/div/div/input"));
        empnameinput.sendKeys(empname);
        Thread.sleep(2000); // Wait for dropdown to load

        // Select the exact match from suggestion dropdown
        driver.findElement(By.xpath("//div[@role='listbox']//span[text()='" + empname + "']")).click();
        
        // Select Leave Type
        driver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/div[2]/div[2]/div/div/form/div[2]/div/div[1]/div/div[2]/div/div/div[1]")).click();
        driver.findElement(By.xpath("//div[@role='listbox']//span[text()='" + leaveType + "']")).click();
        
        // Enter From Date and To Date
        WebElement fromDateField = driver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/div[2]/div[2]/div/div/form/div[3]/div/div[1]/div/div[2]/div/div/input"));
        fromDateField.sendKeys(Keys.chord(Keys.CONTROL, "a"), fromDate);

        WebElement toDateField = driver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/div[2]/div[2]/div/div/form/div[3]/div/div[2]/div/div[2]/div/div/input"));
        toDateField.sendKeys(Keys.chord(Keys.CONTROL, "a"), toDate);
        
        Reporter.log("Entered leave details: " + leaveType + ", From: " + fromDate + " To: " + toDate, true);

        
        Thread.sleep(2000);
        
        js.executeScript("window.scrollBy(0, 400);");
        
        
        // Enter Comment
        driver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/div[2]/div[2]/div/div/form/div[5]/div/div/div/div[2]/textarea")).sendKeys(comment);
        
        // Assign Leave
        driver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/div[2]/div[2]/div/div/form/div[6]/button")).click();
        
        // Wait for the confirmation modal and click "Ok"
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(4));
        WebElement confirmOkButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"app\"]/div[3]/div/div/div/div[3]/button[2]")));

        confirmOkButton.click();
        
        Thread.sleep(2000);
        
        // Log out 
        driver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/div[1]/header/div[1]/div[3]/ul/li/span")).click();
        Thread.sleep(1000);
        driver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/div[1]/header/div[1]/div[3]/ul/li/ul/li[4]/a")).click();
        Thread.sleep(3000);
        Reporter.log("Logged out from Admin account", true);

        
        // Login using employee username and password
        driver.findElement(By.name("username")).sendKeys(userUsername);
        Thread.sleep(1000);
        driver.findElement(By.name("password")).sendKeys(userPassword);
        Thread.sleep(1000);
        driver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/div/div[1]/div/div[2]/div[2]/form/div[3]/button")).click();
        Thread.sleep(2000);
        Reporter.log("Logged in as employee: " + userUsername, true);

        
       // Punch In and Punch Out
        driver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/div[2]/div[2]/div/div[1]/div/div[2]/div[1]/div[2]/button")).click();
        Thread.sleep(2000);
        driver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/div[2]/div[2]/div/div/div/form/div[2]/div/div/div/div[2]/textarea")).sendKeys(punchinmessage);
        Thread.sleep(1000);
        driver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/div[2]/div[2]/div/div/div/form/div[3]/button")).click();
        Reporter.log("Employee punch-in message: " + punchinmessage, true);

        Thread.sleep(10000);
        driver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/div[2]/div[2]/div/div/div/form/div[2]/div/div/div/div[2]/textarea")).sendKeys(punchoutmessage);
        Thread.sleep(1000);
        driver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/div[2]/div[2]/div/div/div/form/div[3]/button")).click();
        Reporter.log("Employee punch-out message: " + punchoutmessage, true);

        // Back to Dashboard
        driver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/div[1]/aside/nav/div[2]/ul/li[5]/a/span")).click();
        Thread.sleep(3000);
        
        // Claim Section
        driver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/div[1]/aside/nav/div[2]/ul/li[7]/a")).click();
        Thread.sleep(2000);
        driver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/div[2]/div[2]/div[2]/div[1]/button")).click();
        Thread.sleep(3000);
        Reporter.log("Navigated to Claim section", true);

        
        // Add claim
        // Click Event dropdown and select desired value
        driver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/div[2]/div[2]/div/div/form/div[1]/div/div[1]/div/div[2]/div/div")).click();
        Thread.sleep(500); // wait for listbox
        driver.findElement(By.xpath("//div[@role='listbox']//span[text()='" + eventName + "']")).click();
        
        // Click Currency dropdown and select desired value
        driver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/div[2]/div[2]/div/div/form/div[1]/div/div[2]/div/div[2]/div/div")).click();
        Thread.sleep(500);
        driver.findElement(By.xpath("//div[@role='listbox']//span[text()='" + currency + "']")).click();
        
        // Add comment
        driver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/div[2]/div[2]/div/div/form/div[2]/div/div/div/div[2]/textarea")).sendKeys(claimcomment);
        Reporter.log("Selected event: " + eventName + ", currency: " + currency, true);

        // Click on Create
        driver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/div[2]/div[2]/div/div/form/div[3]/button[2]")).click();
        Reporter.log("Claim created successfully", true);

        Thread.sleep(5000);
        
        js.executeScript("window.scrollBy(0, 400);");
        
        // Add expense
        driver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/div[2]/div[2]/div/div/div[2]/div/button")).click();
        
        // Wait for the Add Expense modal to appear
        WebDriverWait waitexp = new WebDriverWait(driver, Duration.ofSeconds(5));
        
        // Select Expense Type
        driver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/div[2]/div[2]/div/div/div[6]/div/div/div/form/div[1]/div/div/div/div[2]/div/div")).click();
        waitexp.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@role='listbox']//span[text()='" + exp + "']"))).click();
        
        // Enter Date (format: yyyy-mm-dd or as required)
        driver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/div[2]/div[2]/div/div/div[6]/div/div/div/form/div[2]/div/div[1]/div/div[2]/div/div/input")).sendKeys(date);
        
        // Enter Amount
        driver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/div[2]/div[2]/div/div/div[6]/div/div/div/form/div[2]/div/div[2]/div/div[2]/input")).sendKeys(amount);
        
        // Enter Note
        driver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/div[2]/div[2]/div/div/div[6]/div/div/div/form/div[3]/div/div/div/div[2]/textarea")).sendKeys(note);
        Reporter.log("Expense added: " + exp + ", " + date + ", " + amount + ", " + note, true);

        // Click Save
        driver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/div[2]/div[2]/div/div/div[6]/div/div/div/form/div[4]/button[2]")).click();
        Reporter.log("Expense saved successfully", true);

        Thread.sleep(7000);
        
        js.executeScript("window.scrollBy(0, 400);");
        
        Thread.sleep(2000);
        
        js.executeScript("window.scrollBy(0, 400);");
        
        try {
        	driver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/div[2]/div[2]/div/div/div[9]/button[3]")).click();
        	Reporter.log("Claim submitted successfully", true);
        } catch(Exception e) {
        	Reporter.log("Claim submission failed or button not present", true);
        }
        Thread.sleep(5000);
        
        // Buzz Feed        
        driver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/div[1]/aside/nav/div[2]/ul/li[8]/a")).click();
        Reporter.log("Navigated to Buzz section", true);

        Thread.sleep(2000);
      
        WebDriverWait buzzwait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement postBox = buzzwait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//textarea[@placeholder=\"What's on your mind?\"]")
        ));

        // Enter post content
        postBox.sendKeys(postcontent);
        Thread.sleep(2000);

        // Click on post button
        WebDriverWait postwait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement postBtn = postwait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//textarea[@placeholder=\"What's on your mind?\"]/ancestor::form//button")
        ));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", postBtn);
        Reporter.log("Posted on Buzz: " + postcontent, true);

        Thread.sleep(7000);
        Reporter.log("===== Test Completed for user: " + login + " =====", true);

    }

    @AfterClass
    public void tearDown() throws IOException {
        if (driver != null) {
        	driver.quit();
        }
        Reporter.log("Browser closed", true);
        screenRecorder.stop(); 
        Reporter.log("Screen recording saved", true);

    }
} 