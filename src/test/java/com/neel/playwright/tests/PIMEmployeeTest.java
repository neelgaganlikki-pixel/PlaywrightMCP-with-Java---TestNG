package com.neel.playwright.tests;

import com.neel.playwright.base.BaseTest;
import com.neel.playwright.pages.LoginPage;
import com.neel.playwright.pages.PIMPage;
import com.neel.playwright.utils.EmployeeTestData;
import com.neel.playwright.utils.JsonUtils;
import com.neel.playwright.utils.TestDataGenerator;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Files;
import java.nio.file.Path;

public class PIMEmployeeTest extends BaseTest {
    @Test
    public void createVerifyAndDeleteEmployee() throws Exception {
        EmployeeTestData data = TestDataGenerator.generate();
        Path photo = Path.of(EmployeeTestData.PHOTO_PATH);
        Assert.assertTrue(Files.isRegularFile(photo), "Required employee photo is missing: " + photo);

        LoginPage loginPage = new LoginPage(page);
        PIMPage pimPage = new PIMPage(page);
        loginPage.navigateToLoginPage();
        loginPage.enterUsername(EmployeeTestData.ADMIN_USERNAME);
        loginPage.enterPassword(EmployeeTestData.ADMIN_PASSWORD);
        loginPage.clickLogin();
        pimPage.deleteUserIfExists(EmployeeTestData.EMPLOYEE_USERNAME);
        page.locator("a[href='/web/index.php/pim/viewPimModule']").click();
        pimPage.openEmployeeList();
        pimPage.openAddEmployee();
        pimPage.fillAddEmployee(data.firstName, data.middleName, data.lastName, data.employeeId, photo);
        pimPage.createLoginDetails(EmployeeTestData.EMPLOYEE_USERNAME, EmployeeTestData.EMPLOYEE_PASSWORD);
        pimPage.save();
        pimPage.verifySuccessToast("Successfully");
        pimPage.waitForPersonalDetails();
        data.nationality = pimPage.selectRandomByLabel("Nationality");
        data.maritalStatus = pimPage.selectRandomByLabel("Marital Status");
        data.bloodType = pimPage.selectRandomByLabel("Blood Type");
        pimPage.fillPersonalDetails(data.employeeId, data.otherId, data.driverLicenseNumber,
                data.licenseExpiryDate, data.dateOfBirth);
        pimPage.selectGender(data.gender);
        pimPage.selectByLabel("Nationality", data.nationality);
        pimPage.selectByLabel("Marital Status", data.maritalStatus);
        if (!data.bloodType.startsWith("Unavailable")) {
            pimPage.selectByLabel("Blood Type", data.bloodType);
        }
        pimPage.fillTestField(data.testField);
        JsonUtils.writeEmployeeData(data);
        JsonUtils.writeEmployeeDataText(data);
        pimPage.save();
        pimPage.verifySuccessToast("Successfully");
        pimPage.openEmployeeList();
        pimPage.searchEmployee(data.employeeId);
        Assert.assertTrue(pimPage.employeeRow(data.employeeId).innerText().contains(data.firstName));
        Assert.assertTrue(pimPage.employeeRow(data.employeeId).innerText().contains(data.lastName));
        pimPage.deleteEmployee(data.employeeId);
        pimPage.verifySuccessToast("Successfully");
        pimPage.openEmployeeList();
        pimPage.searchEmployee(data.employeeId);
        Assert.assertFalse(pimPage.employeeExists(data.employeeId), "Employee still exists after deletion");
    }
}