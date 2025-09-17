package com.kriir.platform;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class TestSimpleEndpoint {

    @Test
    public void testSimpleEndpoint() {
        given()
            .when()
            .get("/api/assets/test")
            .then()
            .statusCode(200);
    }
}