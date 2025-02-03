package ru.netology.authorization.data;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import lombok.Data;
import lombok.Value;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class DataHelper {
    private static final RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("http://localhost")
            .setPort(9999)
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();

    private DataHelper() {
    }

    private static void sendRequesttoAuth(AuthInfo user) {
        given()
                .spec(requestSpec)
                .body(user)
                .when().log().all()
                .post("/api/auth")
                .then().log().all()
                .statusCode(200);

    }

    private static String sendRequesttoVerify(VerifyInfo user) {
        String token = given()
                .spec(requestSpec)
                .body(user)
                .when().log().all()
                .post("/api/auth/verification")
                .then().log().all()
                .statusCode(200)
                .extract()
                .path("token");
        assertThat(token, equalTo("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJsb2dpbiI6InZhc3lhIn0.JmhHh8NXwfqktXSFbzkPohUb90gnc3yZ9tiXa0uUpRY"));
        return token;
    }

    private static void sendRequestForTransaction(TransferInfo transReq, String token) {
        given()
                .spec(requestSpec)
                .auth().oauth2(token)
                .body(transReq)
                .when().log().all()
                .post("api/transfer")
                .then().log().all()
                .statusCode(200);
    }


    @Value
    public static class AuthInfo {
        private String login;
        private String password;
    }

    @Value
    public static class VerifyInfo {
        private String login;
        private String code;
    }

    @Data
    public static class CardResponseInfo {
        String id;
        String number;
        int balance;
    }
    @Value
    public static class TransferInfo {
        String from;
        String to;
        int amount;
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


    public static AuthInfo getExsistingUser() {
        return new AuthInfo("vasya", "qwerty123");
    }



    public static AuthInfo getRegisteredUser() {
        var registeredUser = getExsistingUser();
        sendRequesttoAuth(registeredUser);
        return registeredUser;
    }

    public static String getVerificationCode() {
        var verifiedUser = new VerifyInfo("vasya", SQLHelper.getVerificationCode());
        return sendRequesttoVerify(verifiedUser);
    }

    public static CardResponseInfo[] getCardsInfo(String token) {
        return checkCards(token);
    }

    public static TransferInfo createTransReqFrom1to2Card(CardResponseInfo[] cards, int amount) {
        String cardFromTransfer = cards[1].getNumber();
        String cardToTransfer = cards[0].getNumber();
        return new TransferInfo(cardFromTransfer, cardToTransfer, amount);

    }

    public static TransferInfo createTransReqFrom2to1Card(CardResponseInfo[] cards, int amount) {
        String cardFromTransfer = cards[0].getNumber();
        String cardToTransfer = cards[1].getNumber();
        return new TransferInfo(cardFromTransfer, cardToTransfer, amount);

    }

    public static CardResponseInfo[] makeTrasferMoney(TransferInfo transReq, String token){
        sendRequestForTransaction(transReq, token);
        return checkCards(token);
    }
}

//SQLHelper.getVerificationCode()

