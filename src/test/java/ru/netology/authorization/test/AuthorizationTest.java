package ru.netology.authorization.test;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import ru.netology.authorization.data.DataHelper;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.netology.authorization.data.DataHelper.*;
import static ru.netology.authorization.data.SQLHelper.cleanAuthCodes;
import static ru.netology.authorization.data.SQLHelper.cleanDB;


public class AuthorizationTest {

    @AfterAll
    static void cleanAll() {
        cleanDB();
    }

    @AfterEach
    void cleanAuthorCodes() {
        cleanAuthCodes();
    }

    @BeforeEach
    void start() {
        open("http://localhost:9999");
    }

    @Test
    void shouldSuccefullyAuthorized(){
        getRegisteredUser();
        int amountForTransfer = 4000;
        var token = getVerificationCode();
        var cards = getCardsInfo(token);
        int firstCardBalanceBeforeTransaction = cards[1].getBalance();
        int secondCardBalanceBeforeTransaction = cards[0].getBalance();
        var cardsInfoAfterTransaction = makeTrasferMoney(createTransReqFrom1to2Card(cards, amountForTransfer), token);
        int firstCardBalanceAfterTransaction = cardsInfoAfterTransaction[1].getBalance();
        int secondCardBalanceAfterTransaction = cardsInfoAfterTransaction[0].getBalance();
        assertAll(() -> assertEquals(firstCardBalanceBeforeTransaction + amountForTransfer, firstCardBalanceAfterTransaction),
                () -> assertEquals(secondCardBalanceBeforeTransaction - amountForTransfer, secondCardBalanceAfterTransaction));

    }

//    @Test
//    void shouldNotAuthorizedWithInvalidUser(){
//        loginPage.login(getRandomUser());
//        loginPage.checkErrorMessage("Ошибка! \nНеверно указан логин или пароль");
//    }
//
//    @Test
//    void shouldNotAuthorizedWithInvalidVerificationCode() {
//        var verificationPage = loginPage.validLogin(authInfo);
//        verificationPage.verify(generateVerificationCode().getVerificationCode());
//        verificationPage.checkErrorMessage("Ошибка! \nНеверно указан код! Попробуйте ещё раз.");
//    }


}
