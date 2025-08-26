package ru.company.project.data;

import lombok.SneakyThrows;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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
     * Устанавливает и возвращает соединение с тестовой базой данных.
     * Для работы требуются системные свойства: db.url, db.user, db.pass
     *
     * @return активное соединение JDBC с базой данных
     * @throws SQLException если не удалось установить соединение
     *                      (неверные параметры, недоступность БД, сетевые проблемы)
     */
    @SneakyThrows
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    /**
     * Возвращает статус последнего платежа ("APPROVED"/"DECLINED")
     */
    @SneakyThrows
    public static String getPaymentStatus() throws SQLException {
        return getSingleResult("SELECT status FROM payment_entity ORDER BY created DESC LIMIT 1");
    }

    /**
     * Возвращает статус последней кредитной заявки ("APPROVED"/"DECLINED")
     */
    @SneakyThrows
    public static String getCreditStatus() throws SQLException {
        return getSingleResult("SELECT status FROM credit_request_entity ORDER BY created DESC LIMIT 1");
    }

    /**
     * Возвращает общее количество заказов в БД
     */
    @SneakyThrows
    public static long getOrderCount() throws SQLException {
        return getSingleResult("SELECT COUNT(*) FROM order_entity");
    }

    /**
     * Выполняет SQL-запрос и возвращает скалярный результат.
     *
     * @param query SQL-запрос для выполнения
     * @param <T>   тип возвращаемого значения
     * @return результат первого столбца первой строки (может быть null)
     * @throws SQLException если произошла ошибка при выполнении запроса
     */
    @SneakyThrows
    private static <T> T getSingleResult(String query) throws SQLException {
        try (var conn = getConnection()) {
            return runner.query(conn, query, new ScalarHandler<>());
        }
    }

    /**
     * Очищает тестовые таблицы (payment_entity, order_entity, credit_request_entity).
     * Выполняет DELETE запросы для очистки тестовых данных.
     *
     * @throws RuntimeException если произошла ошибка при очистке базы данных
     */
    @SneakyThrows
    public static void cleanDatabase() {
        try (var conn = getConnection()) {
            runner.execute(conn, "DELETE FROM credit_request_entity");
            runner.execute(conn, "DELETE FROM order_entity");
            runner.execute(conn, "DELETE FROM payment_entity");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}