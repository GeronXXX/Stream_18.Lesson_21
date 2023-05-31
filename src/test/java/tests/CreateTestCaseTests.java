package tests;

import com.github.javafaker.Faker;
import models.CreateTestCaseBodyModel;
import models.CreateTestCaseResponseModel;
import org.junit.jupiter.api.Test;
import pages.LoginPage;
import pages.TestCasesPage;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static specs.Specs.requestSpec;
import static specs.Specs.responseSpec;
import static tests.TestData.*;

public class CreateTestCaseTests extends TestBase{

    LoginPage loginPage = new LoginPage();
    TestCasesPage testCasesPage = new TestCasesPage();
    Faker faker = new Faker();

    @Test
    void createWithUiOnlyTest() {
        String testCaseName = faker.name().fullName();

        step("Authorize", () -> {
            loginPage.openPage()
                    .setLogin(login)
                    .setPassword(password)
                    .clickSubmit();
            testCasesPage.checkSideMenu();
        });

        step("Go to project", () ->
                testCasesPage.openProjectPage());

        step("Create test case", () ->
                testCasesPage.setTestcaseName(testCaseName));

        step("Check test case name", () ->
                testCasesPage.checkTestCaseNameAtSideBar(testCaseName));
    }

    @Test
    void createWithApiAndUiTest() {
        String testCaseName = faker.name().fullName();

        CreateTestCaseBodyModel testCaseBody = new CreateTestCaseBodyModel();
        testCaseBody.setName(testCaseName);

        step("Authorize", () -> {
            loginPage.openPage()
                    .setLogin(login)
                    .setPassword(password)
                    .clickSubmit();
            testCasesPage.checkSideMenu();
        });

        step("Go to project", () ->
                testCasesPage.openProjectPage());

        CreateTestCaseResponseModel createTestCaseResponse = step("Create testcase", () ->
                given(requestSpec)
                        .body(testCaseBody)
                        .queryParam("projectId", projectId)
                        .when()
                        .post("/testcasetree/leaf")
                        .then()
                        .spec(responseSpec)
                        .extract().as(CreateTestCaseResponseModel.class));

        Integer testCaseId = createTestCaseResponse.getId();

        step("Open test case editor", () ->
                testCasesPage.openTestCaseEditor(projectId, testCaseId));

        step("Check test case name", () ->
                testCasesPage.checkTestCaseNameInEditor(testCaseName));
    }

    @Test
    void createWitApiOnlyTest() {
        Faker faker = new Faker();
        String testCaseName = faker.name().fullName();

        step("Authorize", () -> {
            loginPage.openPage()
                    .setLogin(login)
                    .setPassword(password)
                    .clickSubmit();
            testCasesPage.checkSideMenu();
        });

        step("Go to project", () ->
                testCasesPage.openProjectPage());

        step("Create testcase", () -> {
            CreateTestCaseBodyModel testCaseBody = new CreateTestCaseBodyModel();
            testCaseBody.setName(testCaseName);

            given()
                    .log().all()
                    .header("X-XSRF-TOKEN", "0a1578fc-9cbf-4dce-8429-55ef2ce0c4e2")
                    .cookies("XSRF-TOKEN", "0a1578fc-9cbf-4dce-8429-55ef2ce0c4e2",
                            "ALLURE_TESTOPS_SESSION", "d61aa6a9-9898-4581-a0ec-470d832ca94e")
                    .contentType("application/json;charset=UTF-8")
                    .body(testCaseBody)
                    .queryParam("projectId", projectId)
                    .when()
                    .post("/api/rs/testcasetree/leaf")
                    .then()
                    .log().status()
                    .log().body()
                    .statusCode(200)
                    .body("statusName", is("Draft"))
                    .body("name", is(testCaseName));
        });
    }
}
