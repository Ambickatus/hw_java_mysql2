package ru.netology.authorization.data;

import lombok.SneakyThrows;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

public class SQLHelper {
    private static final QueryRunner QUERY_RUNNER = new QueryRunner();

    private SQLHelper() {

    }

    @SneakyThrows
    private static Connection getConnect() {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/app", "app", "pass");
    }

    @SneakyThrows
    public static String getVerificCode() {
        var verificCodeStringSQL = "SELECT code FROM auth_codes ORDER BY created DESC LIMIT 1";
        try (var connect = getConnect()) {
            return QUERY_RUNNER.query(connect, verificCodeStringSQL, new ScalarHandler<>());
        }
    }

    @SneakyThrows
    public static List<DataHelper.CardsInfo> getCardNumbers() {
        var cardNumbersList = "SELECT * FROM cards;";
        try (var connect = getConnect()) {
            List<DataHelper.CardsInfo> allNumbers = QUERY_RUNNER.query(connect, cardNumbersList, new BeanListHandler<>(DataHelper.CardsInfo.class));
            return allNumbers;
        }
    }

    @SneakyThrows
    public static void cleanDB() {
        try (var connect = getConnect()) {
            QUERY_RUNNER.execute(connect, "DELETE FROM card_transactions");
            QUERY_RUNNER.execute(connect, "DELETE FROM auth_codes");
            QUERY_RUNNER.execute(connect, "DELETE FROM cards");
            QUERY_RUNNER.execute(connect, "DELETE FROM users");
        }
    }

    @SneakyThrows
    public static void cleanAuthCodes() {
        try (var connect = getConnect()) {
            QUERY_RUNNER.execute(connect, "DELETE FROM auth_codes");
        }
    }

}
