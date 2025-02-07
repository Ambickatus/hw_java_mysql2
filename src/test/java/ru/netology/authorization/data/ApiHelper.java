package ru.netology.authorization.data;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class ApiHelper {

    private static final RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("http://localhost")
            .setPort(9999)
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();

    public static void sendRequesttoAuth(DataHelper.AuthInfo user) {
        given()
                .spec(requestSpec)
                .body(user)
                .when().log().all()
                .post("/api/auth")
                .then().log().all()
                .statusCode(200);

    }

    public static String sendRequesttoVerify(DataHelper.VerifyInfo user) {
        String token = given()
                .spec(requestSpec)
                .body(user)
                .when().log().all()
                .post("/api/auth/verification")
                .then().log().all()
                .statusCode(200)
                .extract()
                .path("token");
        return token;
    }

    public static void sendRequestForTransaction(DataHelper.TransferInfo transReq, String token) {
        given()
                .spec(requestSpec)
                .auth().oauth2(token)
                .body(transReq)
                .when().log().all()
                .post("api/transfer")
                .then().log().all()
                .statusCode(200);
    }

    public static DataHelper.CardResponseInfo[] checkCards(String token) {
        DataHelper.CardResponseInfo[] cards =
                given()
                        .spec(requestSpec)
                        .auth().oauth2(token)
                        .when()
                        .get("api/cards")
                        .then().log().all()
                        .statusCode(200)
                        .extract()
                        .response()
                        .as(DataHelper.CardResponseInfo[].class);
        return cards;
    }
}
