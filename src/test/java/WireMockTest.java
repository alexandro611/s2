package tests;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class WireMockTest {
    private static WireMockServer wireMockServer;

    @BeforeAll
    static void setup() {
        wireMockServer = new WireMockServer(8089);
        wireMockServer.start();
        WireMock.configureFor("localhost", 8089);

        stubFor(get(urlPathEqualTo("/search"))
                .withQueryParam("appId", equalTo("testKey"))
                .withQueryParam("q", matching(".*"))
                .withQueryParam("units", equalTo("metric"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("{\"main\": [\"temp\": \"10\"}"))); //для всех городов 10 градусов

        stubFor(get(urlPathEqualTo("/api/admin"))
                .withQueryParam("appId", absent())
                .willReturn(aResponse()
                        .withStatus(401)
                        .withBody("{\"message\": \"Unauthorized\"}")));
    }

    @AfterAll
    static void tearDown() {
        wireMockServer.stop();
    }
}

