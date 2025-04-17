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
        wireMockServer = new WireMockServer(8080);
        wireMockServer.start();
        WireMock.configureFor("localhost", 8080);

        stubFor(get(urlEqualTo("/api/balance"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"balance\": 100}")));
    }

    @AfterAll
    static void tearDown() {
        wireMockServer.stop();
    }
}

