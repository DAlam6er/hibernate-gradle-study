package com.dmdev;

import com.dmdev.converter.AgeConverter;
import com.dmdev.entity.AgeType;
import com.dmdev.entity.PersonalInfo;
import com.dmdev.entity.Role;
import com.dmdev.entity.User;
import org.junit.jupiter.api.Test;

import javax.persistence.Column;
import javax.persistence.Table;
import java.lang.reflect.Field;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;

class HibernateRunnerTest {
    private final static String DB_URL = "jdbc:postgresql://localhost:5432/postgres";
    private final static String DB_USER_NAME = "postgres";
    private final static String DB_USER_PASS = "postgres";

    User user = User.builder()
            .username("petr@gmail.com")
            .personalInfo(PersonalInfo.builder()
                    .firstname("Petr")
                    .lastname("Petrov")
                    .birthDate(new AgeType(LocalDate.of(1999, 1, 31)))
                    .build())
            .role(Role.USER)
            .build();

    @Test
    void checkSaveReflectionApi() throws SQLException, IllegalAccessException {
        var sql = """
                insert
                into
                    %s
                    (%s)
                values
                    (%s)
                """;

        var tableName = ofNullable(
                user.getClass().getAnnotation(Table.class))
                .map(tableAnnotation -> tableAnnotation.schema() + "." + tableAnnotation.name())
                .orElse(user.getClass().getName());

        // Внимание! getDeclaredFields() не гарантирует порядок полей
        // поэтому в идеале следовало бы отсортировать их по названию
        Field[] declaredFields = user.getClass().getDeclaredFields();

        var columnNames = Arrays.stream(declaredFields)
                .map(field -> ofNullable(field.getAnnotation(Column.class))
                        .map(Column::name)
                        .orElse(field.getName()))
                .collect(joining(", "));
        // В реальном коде ситуация будет несколько сложнее, т.к. мы можем передавать
        // PhysicalNamingStrategy и в этом случае мы получаем у него имя field, как оно должно быть

        var columnValues = Arrays.stream(declaredFields)
                .map(field -> "?")
                .collect(joining(", "));

        var formattedSql = sql.formatted(tableName, columnNames, columnValues);
        System.out.println(formattedSql);

        try (var connection = DriverManager.getConnection(
                DB_URL, DB_USER_NAME, DB_USER_PASS);
             var preparedStatement = connection.prepareStatement(formattedSql)) {
            for (int i = 0; i < declaredFields.length; i++) {
                declaredFields[i].setAccessible(true);
                // field.get(user) — получить значение поля, представленного этим Field, для указанного объекта (user)
                // user - объект, из которого должно быть извлечено значение представляемого поля
                // возвращает значение представляемого поля в объекте user;
                // примитивные значения упаковываются в соответствующий объект перед возвратом

                var object = declaredFields[i].get(user);
                if (object instanceof AgeType birthday) {
                    preparedStatement.setDate(
                            i + 1,
                            new AgeConverter().convertToDatabaseColumn(birthday));
                } else if (object instanceof Role role) {
                    preparedStatement.setString(i + 1, role.name());
                } else {
                    preparedStatement.setObject(i + 1, object);
                }
            }
            preparedStatement.executeUpdate();
        }
    }
}