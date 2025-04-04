package ru.company.project.data;

import lombok.SneakyThrows;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Утилитный класс для работы с тестовой базой данных.
 * Для работы требуются системные свойства: db.url, db.user, db.pass.
 */
public class DBHelper {
    private static final QueryRunner runner = new QueryRunner();
    private static final String url = System.getProperty("db.url");
    private static final String user = System.getProperty("db.user");
    private static final String password = System.getProperty("db.pass");

    /**
     * Возвращает соединение с БД
     * @throws RuntimeException если соединение не установлено
     */
    @SneakyThrows
    public static Connection getConnection() {
        return DriverManager.getConnection(url, user, password);
    }

    /** Возвращает статус последнего платежа ("APPROVED"/"DECLINED") */
    @SneakyThrows
    public static String getPaymentStatus() {
        return getSingleResult("SELECT status FROM payment_entity ORDER BY created DESC LIMIT 1");
    }

    /** Возвращает статус последней кредитной заявки ("APPROVED"/"DECLINED") */
    @SneakyThrows
    public static String getCreditStatus() {
        return getSingleResult("SELECT status FROM credit_request_entity ORDER BY created DESC LIMIT 1");
    }

    /** Возвращает общее количество заказов в БД */
    @SneakyThrows
    public static long getOrderCount() {
        return getSingleResult("SELECT COUNT(*) FROM order_entity");
    }

    /**
     * Выполняет SQL-запрос и возвращает результат
     * @param query SQL-запрос
     * @return результат первого столбца первой строки
     */
    @SneakyThrows
    private static <T> T getSingleResult(String query) {
        try (var conn = getConnection()) {
            return runner.query(conn, query, new ScalarHandler<>());
        }
    }

    /** Очищает тестовые таблицы (payment_entity, order_entity, credit_request_entity) */
    @SneakyThrows
    public static void cleanDatabase() {
        try (var conn = getConnection()) {
            runner.execute(conn, "DELETE FROM credit_request_entity");
            runner.execute(conn, "DELETE FROM order_entity");
            runner.execute(conn, "DELETE FROM payment_entity");
        }
    }
}