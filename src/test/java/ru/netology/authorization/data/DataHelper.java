package ru.netology.authorization.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;

import static ru.netology.authorization.data.ApiHelper.*;

public class DataHelper {


    private DataHelper() {
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

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CardsInfo {
        String id;
        String user_id;
        String number;
        int balance_in_kopecks;
    }


    public static AuthInfo getExsistingUser() {
        return new AuthInfo("vasya", "qwerty123");
    }


    public static AuthInfo getRegisteredUser() {
        var registeredUser = getExsistingUser();
        ApiHelper.sendRequesttoAuth(registeredUser);
        return registeredUser;
    }

    public static String getVerificationCode() {
        var verifiedUser = new VerifyInfo("vasya", SQLHelper.getVerificCode());
        return sendRequesttoVerify(verifiedUser);
    }


    public static TransferInfo createTransReqFrom1to2Card(List<CardsInfo> cardsInfo, int amount) {
        String cardFromTransfer = cardsInfo.get(1).number;
        String cardToTransfer = cardsInfo.get(0).number;
        return new TransferInfo(cardFromTransfer, cardToTransfer, amount);

    }

    public static TransferInfo createTransReqFrom2to1Card(List<CardsInfo> cardsInfo, int amount) {
        String cardFromTransfer = cardsInfo.get(0).number;
        String cardToTransfer = cardsInfo.get(1).number;
        return new TransferInfo(cardFromTransfer, cardToTransfer, amount);

    }

    public static CardResponseInfo[] makeTrasferMoney(TransferInfo transReq, String token) {
        sendRequestForTransaction(transReq, token);
        return checkCards(token);
    }
}


