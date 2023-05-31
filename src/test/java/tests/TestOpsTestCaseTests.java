package tests;

import com.github.javafaker.Faker;
import models.AddDescriptionBodyModel;
import models.AddDescriptionResponseModel;
import models.CreateTestCaseBodyModel;
import models.CreateTestCaseResponseModel;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import pages.LoginPage;
import pages.TestCasesPage;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static specs.Specs.requestSpec;
import static specs.Specs.responseSpec;
import static tests.TestData.*;

public class TestOpsTestCaseTests extends TestBase{

    TestCasesPage testCasesPage = new TestCasesPage();
    LoginPage loginPage = new LoginPage();
    Faker faker = new Faker();

    String testCaseNameInitial = faker.artist().name();
    String testCaseNameUpdated = faker.book().author();
    String testCaseDescriptionInitial = faker.address().fullAddress();
    String testCaseDescriptionUpdated = faker.color().name();
    String stepNameInitial = faker.book().title();


    @Tag("Обновление")
    @Test
    void updateTestCaseNameAndDescriptionTest() {
        CreateTestCaseBodyModel createTestCaseBody = new CreateTestCaseBodyModel();
        createTestCaseBody.setName(testCaseNameInitial);

        step("Авторизация", () -> {
            loginPage.openPage()
                    .setLogin(login)
                    .setPassword(password)
                    .clickSubmit();
            testCasesPage.checkSideMenu();
        });

        step("Перейти к проекту", () ->
                testCasesPage.openProjectPage());

        CreateTestCaseResponseModel createTestCaseResponse = step("Create testcase", () ->
                given(requestSpec)
                        .body(createTestCaseBody)
                        .queryParam("projectId", projectId)
                        .when()
                        .post("/testcasetree/leaf")
                        .then()
                        .spec(responseSpec)
                        .extract().as(CreateTestCaseResponseModel.class));

        Integer testCaseId = createTestCaseResponse.getId();

        AddDescriptionBodyModel addDescriptionBody = new AddDescriptionBodyModel();
        addDescriptionBody.setId(testCaseId);
        addDescriptionBody.setDescription(testCaseDescriptionInitial);
        addDescriptionBody.setDescriptionHtml(null);

        AddDescriptionResponseModel addDescriptionResponse = step("Добавление описания к тест-кейсу", () ->
                given(requestSpec)
                        .body(addDescriptionBody)
                        .when()
                        .patch("/testcase/" + testCaseId)
                        .then()
                        .spec(responseSpec)
                        .extract().as(AddDescriptionResponseModel.class));

        step("Открытие редактора тест-кейса", () ->
                testCasesPage.openTestCaseEditor(projectId, testCaseId));

        step("Проверка имени тест-кейса", () ->
                testCasesPage.checkTestCaseNameInEditor(testCaseNameInitial));

        step("Проверка описания", () ->
                testCasesPage.checkTestCaseDescription(testCaseDescriptionInitial));

        step("Обновление имени тест-кейса", () ->
                testCasesPage.updateTestCaseName(testCaseNameUpdated));

        step("Проверка обновлённого имени тест-кейса", () ->
                testCasesPage.checkTestCaseNameInEditor(testCaseNameUpdated));

        step("Обновить описание", () ->
                testCasesPage.updateTestCaseDescription(testCaseDescriptionUpdated));

        step("Проверка обновлённого описания", () ->
                testCasesPage.checkTestCaseDescription(testCaseDescriptionUpdated));
    }

    @Test
    @Tag("Обновление")
    void addStepToTestCaseTest() {
        CreateTestCaseBodyModel createTestCaseBody = new CreateTestCaseBodyModel();
        createTestCaseBody.setName(testCaseNameInitial);

        step("Авторизация", () -> {
            loginPage.openPage()
                    .setLogin(login)
                    .setPassword(password)
                    .clickSubmit();
            testCasesPage.checkSideMenu();
        });

        step("Перейти к поекту", () ->
                testCasesPage.openProjectPage());

        CreateTestCaseResponseModel createTestCaseResponse = step("Create testcase", () ->
                given(requestSpec)
                        .body(createTestCaseBody)
                        .queryParam("projectId", projectId)
                        .when()
                        .post("/testcasetree/leaf")
                        .then()
                        .spec(responseSpec)
                        .extract().as(CreateTestCaseResponseModel.class));

        Integer testCaseId = createTestCaseResponse.getId();

        step("Открытие редактора тест-кейса", () ->
                testCasesPage.openTestCaseEditor(projectId, testCaseId));

        step("Проверка имени тест-кейса", () ->
                testCasesPage.checkTestCaseNameInEditor(testCaseNameInitial));

        step("Добавление шага в тест-кейс", () ->
                testCasesPage.addStepToTestCase(stepNameInitial));

        step("Проверка имени добавленного шага", () ->
                testCasesPage.checkStepName(stepNameInitial));
    }


}
