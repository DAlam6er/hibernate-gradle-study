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
import java.lang.reflect.InvocationTargetException;
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
    void checkGetReflectionApi() throws SQLException, NoSuchMethodException,
            InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        var id = "ivan@gmail.com";
        var sql = """
                SELECT username, firstname, lastname, birth_date, role, info
                FROM users
                WHERE username = ?
                """;

        try (var connection = DriverManager.getConnection(
                DB_URL, DB_USER_NAME, DB_USER_PASS);
             var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, id);

            var resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Class<User> clazz = User.class;
                // создадим экземпляр класса clazz и установим всего его поля
                // получаем конструктор без параметров
                var constructor = clazz.getConstructor();
                var createdUser = constructor.newInstance();

                // пройдемся по всем полям User и установим поля из ResultSet
                var usernameField = clazz.getDeclaredField("username");
                usernameField.setAccessible(true);
                usernameField.set(createdUser, resultSet.getString("username"));

                var firstnameField = clazz.getDeclaredField("firstname");
                firstnameField.setAccessible(true);
                firstnameField.set(createdUser, resultSet.getString("firstname"));

                var lastnameField = clazz.getDeclaredField("lastname");
                lastnameField.setAccessible(true);
                lastnameField.set(createdUser, resultSet.getString("lastname"));

                System.out.println(createdUser);
            }
        }
    }

    @Test
    void checkSaveReflectionApi() throws SQLException, IllegalAccessException {
        // через Reflection API получим класс этой сущности,
        // чтобы показать, как Session формирует SQL-запрос
        // %s — признак динамической составляющей
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