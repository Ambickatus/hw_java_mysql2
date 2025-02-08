package ru.netology.authorization.test;

import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.netology.authorization.data.DataHelper.*;
import static ru.netology.authorization.data.SQLHelper.*;


public class AuthorizationTest {
    int firstCardBalanceBeforeTransaction;
    int secondCardBalanceBeforeTransaction;
    List<CardsInfo> cardsNumbers;
    String token;

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
        getRegisteredUser();
        token = getVerificationCode();
        cardsNumbers = getCardNumbers();
        firstCardBalanceBeforeTransaction = cardsNumbers.get(1).getBalance_in_kopecks() / 100;
        secondCardBalanceBeforeTransaction = cardsNumbers.get(0).getBalance_in_kopecks() / 100;
    }

    @Test
    @DisplayName("Test1")
    void shouldNotTransferHugeAmountOfMoney() {
        int amountForTransfer = 15000;
        var cardsInfoAfterTransaction = makeTrasferMoney(createTransReqFrom1to2Card(cardsNumbers, amountForTransfer), token);
        int firstCardBalanceAfterTransaction = cardsInfoAfterTransaction[1].getBalance();
        int secondCardBalanceAfterTransaction = cardsInfoAfterTransaction[0].getBalance();
        assertAll(() -> assertEquals(firstCardBalanceBeforeTransaction - amountForTransfer, firstCardBalanceAfterTransaction),
                () -> assertEquals(secondCardBalanceBeforeTransaction + amountForTransfer, secondCardBalanceAfterTransaction));
    }

    @Test
    @DisplayName("Test2")
    void shouldSuccefullyTransferMoneyFrom1to2Card() {
        int amountForTransfer = 4000;
        var cardsInfoAfterTransaction = makeTrasferMoney(createTransReqFrom2to1Card(cardsNumbers, amountForTransfer), token);
        int firstCardBalanceAfterTransaction = cardsInfoAfterTransaction[1].getBalance();
        int secondCardBalanceAfterTransaction = cardsInfoAfterTransaction[0].getBalance();
        assertAll(() -> assertEquals(firstCardBalanceBeforeTransaction + amountForTransfer, firstCardBalanceAfterTransaction),
                () -> assertEquals(secondCardBalanceBeforeTransaction - amountForTransfer, secondCardBalanceAfterTransaction));
    }

    @Test
    @DisplayName("Test3")
    void shouldТNotransferMoneyFromNegativeBalanceto() {
        int amountForTransfer = 3000;
        var cardsInfoAfterTransaction = makeTrasferMoney(createTransReqFrom1to2Card(cardsNumbers, amountForTransfer), token);
        int firstCardBalanceAfterTransaction = cardsInfoAfterTransaction[1].getBalance();
        int secondCardBalanceAfterTransaction = cardsInfoAfterTransaction[0].getBalance();
        assertAll(() -> assertEquals(firstCardBalanceBeforeTransaction - amountForTransfer, firstCardBalanceAfterTransaction),
                () -> assertEquals(secondCardBalanceBeforeTransaction + amountForTransfer, secondCardBalanceAfterTransaction));
    }
}
