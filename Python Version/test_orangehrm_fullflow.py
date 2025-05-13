# test_orangehrm_fullflow.py

import pytest
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
import time
from utils import read_excel_data, upload_file
from screen_recorder import ScreenRecorder

@pytest.fixture(scope="class")
def setup_teardown(request):
    driver = webdriver.Chrome()
    driver.maximize_window()
    driver.implicitly_wait(10)
    driver.get("https://opensource-demo.orangehrmlive.com/")
    recorder = ScreenRecorder("recordings/OrangeHRM_FullFlow.avi")
    recorder.start()
    request.cls.driver = driver
    request.cls.recorder = recorder
    yield
    driver.quit()
    recorder.stop()

@pytest.mark.usefixtures("setup_teardown")
class TestOrangeHRMFullFlow:

    @pytest.mark.parametrize("data", read_excel_data("target/EmployeeData.xlsx"))
    def test_full_flow(self, data):
        driver = self.driver
        wait = WebDriverWait(driver, 10)
        (
            login, loginpassword,
            firstName, middleName, lastName, username, password, photopath,
            licenseNumber, licenseExpiry, ssn, nationality, maritalStatus, dob, bloodType, resumepath, miliserv,
            empFullName, userUsername, userPassword, userRole, userStatus,
            empname, leaveType, fromDate, toDate, comment,
            punchinmessage, punchoutmessage,
            eventName, currency, claimcomment,
            exp, date, amount, note,
            postcontent
        ) = data

        driver.find_element(By.NAME, "username").send_keys(login)
        driver.find_element(By.NAME, "password").send_keys(loginpassword)
        driver.find_element(By.XPATH, "//button[@type='submit']").click()
        time.sleep(3)

        driver.find_element(By.XPATH, "//span[text()='PIM']").click()
        driver.find_element(By.XPATH, "//button[text()=' Add ']").click()
        time.sleep(2)

        driver.find_element(By.NAME, "firstName").send_keys(firstName)
        driver.find_element(By.NAME, "middleName").send_keys(middleName)
        driver.find_element(By.NAME, "lastName").send_keys(lastName)
        time.sleep(1)

        driver.find_element(By.XPATH, "//button/i").click()
        upload_file(photopath)

        driver.find_element(By.XPATH, "//label/span[text()='Create Login Details']").click()
        driver.find_element(By.XPATH, "//label[text()='Username']/../following-sibling::div/input").send_keys(username)
        driver.find_element(By.XPATH, "//label[text()='Password']/../following-sibling::div/input").send_keys(password)
        driver.find_element(By.XPATH, "//label[text()='Confirm Password']/../following-sibling::div/input").send_keys(password)

        driver.find_element(By.XPATH, "//button[text()=' Save ']").click()
        time.sleep(5)

        driver.find_element(By.XPATH, "//input[@placeholder='License Number']").send_keys(licenseNumber)
        driver.find_element(By.XPATH, "//input[@placeholder='License Expiry Date']").send_keys(licenseExpiry)

        driver.execute_script("window.scrollBy(0, 500);")
        driver.find_element(By.XPATH, "//button[text()='Add']").click()
        driver.find_element(By.XPATH, "//div[contains(@class,'oxd-file-button')]").click()
        upload_file(resumepath)
        driver.find_element(By.TAG_NAME, "textarea").send_keys("Resume Attachment")
        driver.find_element(By.XPATH, "//button[text()=' Save ']").click()
        time.sleep(2)

        driver.find_element(By.XPATH, "//a[text()='Admin']").click()
        time.sleep(2)
        driver.find_element(By.XPATH, "//button[text()=' Add ']").click()
        time.sleep(2)

        driver.find_element(By.XPATH, "//label[text()='User Role']/../following-sibling::div//div[text()='-- Select --']").click()
        driver.find_element(By.XPATH, f"//span[text()='{userRole}']").click()

        emp_input = driver.find_element(By.XPATH, "//input[@placeholder='Type for hints...']")
        emp_input.send_keys(empFullName)
        time.sleep(2)
        driver.find_element(By.XPATH, f"//span[text()='{empFullName}']").click()

        driver.find_element(By.XPATH, "//label[text()='Username']/../following-sibling::div/input").send_keys(userUsername)
        driver.find_element(By.XPATH, "//label[text()='Status']/../following-sibling::div//div[text()='-- Select --']").click()
        driver.find_element(By.XPATH, f"//span[text()='{userStatus}']").click()

        driver.find_element(By.XPATH, "//label[text()='Password']/../following-sibling::div/input").send_keys(userPassword)
        driver.find_element(By.XPATH, "//label[text()='Confirm Password']/../following-sibling::div/input").send_keys(userPassword)

        driver.find_element(By.XPATH, "//button[text()=' Save ']").click()
        time.sleep(3)

        driver.find_element(By.XPATH, "//span[text()='Leave']").click()
        time.sleep(1)
        driver.find_element(By.XPATH, "//a[text()='Assign Leave']").click()

        emp_input = driver.find_element(By.XPATH, "//input[@placeholder='Type for hints...']")
        emp_input.send_keys(empname)
        time.sleep(2)
        driver.find_element(By.XPATH, f"//span[text()='{empname}']").click()

        driver.find_element(By.XPATH, "//label[text()='Leave Type']/../following-sibling::div//div[text()='-- Select --']").click()
        driver.find_element(By.XPATH, f"//span[text()='{leaveType}']").click()

        driver.find_element(By.XPATH, "//input[@placeholder='yyyy-mm-dd']").send_keys(Keys.CONTROL, 'a', fromDate)
        driver.find_element(By.XPATH, "(//input[@placeholder='yyyy-mm-dd'])[2]").send_keys(Keys.CONTROL, 'a', toDate)

        driver.find_element(By.TAG_NAME, "textarea").send_keys(comment)
        driver.find_element(By.XPATH, "//button[text()=' Assign ']").click()
        time.sleep(3)

        driver.find_element(By.XPATH, "//span[text()='Time']").click()
        driver.find_element(By.XPATH, "//button[text()=' Punch In ']").click()
        time.sleep(2)
        driver.find_element(By.TAG_NAME, "textarea").send_keys(punchinmessage)
        driver.find_element(By.XPATH, "//button[text()=' Save ']").click()

        time.sleep(8)
        driver.find_element(By.XPATH, "//button[text()=' Punch Out ']").click()
        driver.find_element(By.TAG_NAME, "textarea").send_keys(punchoutmessage)
        driver.find_element(By.XPATH, "//button[text()=' Save ']").click()

        driver.find_element(By.XPATH, "//span[text()='Claim']").click()
        time.sleep(1)
        driver.find_element(By.XPATH, "//button[text()=' Apply ']").click()

        driver.find_element(By.XPATH, "//label[text()='Event']/../following-sibling::div//div[text()='-- Select --']").click()
        driver.find_element(By.XPATH, f"//span[text()='{eventName}']").click()

        driver.find_element(By.XPATH, "//label[text()='Currency']/../following-sibling::div//div[text()='-- Select --']").click()
        driver.find_element(By.XPATH, f"//span[text()='{currency}']").click()

        driver.find_element(By.TAG_NAME, "textarea").send_keys(claimcomment)
        driver.find_element(By.XPATH, "//button[text()=' Save ']").click()
        time.sleep(5)

        driver.find_element(By.XPATH, "//button[text()=' Add Expense ']").click()
        driver.find_element(By.XPATH, "//label[text()='Expense Type']/../following-sibling::div//div[text()='-- Select --']").click()
        driver.find_element(By.XPATH, f"//span[text()='{exp}']").click()

        driver.find_element(By.XPATH, "//input[@placeholder='yyyy-mm-dd']").send_keys(date)
        driver.find_element(By.XPATH, "//label[text()='Amount']/../following-sibling::div/input").send_keys(amount)
        driver.find_element(By.TAG_NAME, "textarea").send_keys(note)
        driver.find_element(By.XPATH, "//button[text()=' Save ']").click()
        time.sleep(3)

        driver.find_element(By.XPATH, "//span[text()='Buzz']").click()
        time.sleep(2)
        driver.find_element(By.XPATH, "//textarea[@placeholder=\"What's on your mind?\"]").send_keys(postcontent)
        driver.find_element(By.XPATH, "//textarea/ancestor::form//button").click()
        time.sleep(5)
