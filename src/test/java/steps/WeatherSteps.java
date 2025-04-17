package steps;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.cucumber.java.*;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.ru.Дано;
import io.cucumber.java.ru.И;
import io.cucumber.java.ru.Когда;
import io.cucumber.java.ru.То;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

public class WeatherSteps {
    private WireMockServer wireMockServer;
    private Response response;
    private String cityName;

    @Before
    public void setup() {
        wireMockServer = new WireMockServer(8089);
        wireMockServer.start();
        RestAssured.baseURI = "http://localhost:8089";
    }

    @After
    public void tearDown() {
        wireMockServer.stop();
    }



    @Given("Я запрашиваю погоду для города {string} без авторизации")
    public void requestWeatherForCityNotAuth(String city) {
        this.cityName = city;
        response = given()
                .queryParam("q", city)
                .queryParam("appid", "ваш_api_ключ") // Замените на реальный ключ
                .queryParam("units", "metric")
                .queryParam("lang", "ru")
                .when()
                .get("/data/2.5/weather");
    }

    @Given("Я запрашиваю погоду для города {string} в неизвестных единицах измерения")
    public void requestWeatherForCityNotValidUnits(String city) {
        this.cityName = city;
        response = given()
                .queryParam("q", city)
                .queryParam("appid", "ваш_api_ключ") // Замените на реальный ключ
                .queryParam("units", "not_valid_metric")
                .queryParam("lang", "ru")
                .when()
                .get("/data/2.5/weather");
    }

    @Given("Я запрашиваю погоду без указания города")
    public void requestWeatherForCityWithotCity(String city) {
        this.cityName = city;
        response = given()
                .queryParam("appid", "ваш_api_ключ") // Замените на реальный ключ
                .queryParam("units", "metric")
                .queryParam("lang", "ru")
                .when()
                .get("/data/2.5/weather");
    }

    @Given("Я запрашиваю погоду для города {string}")
    public void requestWeatherForCity(String city) {
        this.cityName = city;
        response = given()
                .queryParam("q", city)
                .queryParam("appid", "ваш_api_ключ") // Замените на реальный ключ
                .queryParam("units", "metric")
                .queryParam("lang", "ru")
                .when()
                .get("/data/2.5/weather");
    }

    @Then("Я получаю корректные данные о погоде")
    public void verifyWeatherData() {
        assertEquals(200, response.getStatusCode());
        assertNotNull(response.jsonPath().get("main.temp"));
        assertNotNull(response.jsonPath().get("weather[0].description"));
        assertEquals(cityName, response.jsonPath().get("name"));
    }

    @And("Температура отображается в градусах Цельсия")
    public void verifyTemperatureInCelsius() {
        String temperature = response.jsonPath().get("main.temp").toString();
        assertTrue(temperature.matches("-?\\d+(\\.\\d+)?"));
    }

    @Then("Я получаю сообщение об 404 ошибке город не указан")
    public void verifyErrorMessage() {
        assertEquals(404, response.getStatusCode());
        assertEquals("city not found", response.jsonPath().get("message"));
    }

    @Then("Я получаю сообщение об 404 ошибке город не задан")
    public void verifyErrorCity() {
        assertEquals(404, response.getStatusCode());
        assertEquals("q is necessary", response.jsonPath().get("message"));

    }

    @Then("Я получаю сообщение об 404 ошибке единицы измерения неизвестны")
    public void verifyErrorWeatherMetric() {
        assertEquals(404, response.getStatusCode());
        assertEquals("units is not valid", response.jsonPath().get("message"));

    }

    @Then("Я получаю сообщение об 404 ошибке город не задан")
    public void verifyErrorAuth() {
        assertEquals(401, response.getStatusCode());

    }


}