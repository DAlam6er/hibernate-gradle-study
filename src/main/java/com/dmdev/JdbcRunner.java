package com.dmdev;

import com.dmdev.util.ConnectionPool;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

/**
 * 01 Краткая ревизия того, как устроен JDBC, как работает Connection и Connection Pool
 */
public class JdbcRunner {
    public static void main(String[] args) {
        String sql = """
            SELECT id
            FROM flight
            WHERE departure_date BETWEEN ? AND ?
        """;

        LocalDateTime start = LocalDate.of(2020, 1, 1).atStartOfDay();
        LocalDateTime end = LocalDateTime.now(ZoneOffset.UTC);

        List<Long> result = new ArrayList<>();
        // в Hibernate будет Session вместо Connection
        // в Hibernate будет SessionFactory вместо ConnectionPool
        try (var connection = ConnectionPool.get();
             var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setFetchSize(50);
            preparedStatement.setQueryTimeout(10);
            preparedStatement.setMaxRows(100);

            System.out.println(preparedStatement);
            preparedStatement.setTimestamp(1, Timestamp.valueOf(start));
            System.out.println(preparedStatement);
            preparedStatement.setTimestamp(2, Timestamp.valueOf(end));
            System.out.println(preparedStatement);

            var resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                // чтобы избежать лишнего boxing/unboxing, лучше использовать
                // resultSet.getObject() вместо resultSet.getLong()
                result.add(resultSet.getObject("id", Long.class));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        System.out.printf("Flights between %s and %s: %s", start, end, result);
    }
}
