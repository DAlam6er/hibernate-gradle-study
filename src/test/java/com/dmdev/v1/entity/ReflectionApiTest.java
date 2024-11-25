package com.dmdev.v1.entity;

import com.dmdev.util.ConnectionPool;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Stream.generate;

public class ReflectionApiTest {
    private static final String USER_TABLE = "public.users_v1";

    private final User user = User.builder()
            .username("ivan@gmail.com")
            .firstname("Ivan")
            .lastname("Ivanov")
            .birthDate(LocalDate.of(2000, 1, 19))
            .age(24)
            .build();

    @BeforeAll
    static void setUp() {
        try (Connection connection = ConnectionPool.get();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     getSqlScriptBody("scripts/sql/02_create_table_users_v1.sql")
             )) {
            preparedStatement.execute();
        } catch (SQLException _) {
        }
    }

    @AfterEach
    void tearDown() {
        var deleteUserByIdSql = "DELETE FROM %s where username = ?".formatted(USER_TABLE);
        try (Connection connection = ConnectionPool.get();
             PreparedStatement preparedStatement = connection.prepareStatement(deleteUserByIdSql)) {
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Через Reflection API добавим класс сущности,
     * чтобы показать, как Session формирует SQL-запрос
     * (%s — признак динамической составляющей)
     */
    @Test
    void checkInsertReflectionApi() throws SQLException, IllegalAccessException {
        String sql = """
                INSERT INTO %s (%s) VALUES (%s);
                """;

        String tableName = ofNullable(user.getClass().getAnnotation(Table.class))
                .map(tableAnnotation -> "%s.%s".formatted(tableAnnotation.schema(), tableAnnotation.name()))
                .orElse(user.getClass().getName()); // поскольку аннотация @Table является необязательной - её может и не быть

        // getDeclaredFields() не гарантирует порядок полей
        List<Field> declaredFields = Arrays.stream(user.getClass().getDeclaredFields())
                .sorted(Comparator.comparing(Field::getName))
                .toList();

        String columnNames = declaredFields.stream()
                .map(field -> ofNullable(field.getAnnotation(Column.class))
                        .map(Column::name)
                        .orElse(field.getName()))
                .sorted(Comparator.naturalOrder())
                .collect(joining(", "));

        String columnValues = generate(() -> "?")
                .limit(declaredFields.size())
                .collect(joining(", "));

        System.out.println(sql.formatted(tableName, columnNames, columnValues));

        try (Connection connection = ConnectionPool.get();
             PreparedStatement preparedStatement = connection.prepareStatement(sql.formatted(tableName, columnNames, columnValues))
        ) {
            for (int i = 0; i < declaredFields.size(); i++) {
                declaredFields.get(i).setAccessible(true);
                /*
                получить значение поля, представленного этим Field, для указанного объекта (user)
                user - объект, из которого должно быть извлечено значение представляемого поля
                возвращает значение представляемого поля в объекте user;
                примитивные значения упаковываются в соответствующий объект перед возвратом
                */
                var object = declaredFields.get(i).get(user);
                preparedStatement.setObject(i + 1, object);
            }

            preparedStatement.executeUpdate();
        }
    }

    /**
     * Через Reflection API получим класс сущности,
     * чтобы показать, как Session формирует SQL-запрос
     * (%s — признак динамической составляющей)
     */
    @Test
    void checkGetReflectionApi() throws SQLException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        // Key for session
        Class<?> clazz = User.class;
        String id = user.getUsername();

        String getUserSql = "SELECT %s FROM %s WHERE %s;";

        String tableName = ofNullable(clazz.getAnnotation(Table.class))
                .map(tableAnnotation -> "%s.%s".formatted(tableAnnotation.schema(), tableAnnotation.name()))
                .orElse(clazz.getName());

        Field[] declaredFields = clazz.getDeclaredFields(); // не гарантирует порядок полей

        String columnNames = Arrays.stream(declaredFields)
                .map(field -> ofNullable(field.getAnnotation(Column.class))
                        .map(Column::name)
                        .orElse(field.getName()))
                .sorted(Comparator.naturalOrder())
                .collect(joining(", "));

        String condition = String.format("%s = '%s'",
                Arrays.stream(declaredFields)
                        .filter(field -> field.isAnnotationPresent(Id.class))
                        .findFirst()
                        .map(Field::getName)
                        .orElseThrow(() -> new RuntimeException("No column with annotation @Id found!")),
                id
        );

        try (Connection connection = ConnectionPool.get();
             PreparedStatement preparedStatement = connection.prepareStatement(getUserSql.formatted(columnNames, tableName, condition))
        ) {
            saveUser(user, connection);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                var instance = clazz.getConstructor().newInstance();
                for (Field declaredField : declaredFields) {
                    declaredField.setAccessible(true);
                    var fieldName = ofNullable(declaredField.getAnnotation(Column.class))
                            .map(Column::name)
                            .orElse(declaredField.getName());
                    declaredField.set(instance, toJavaType(resultSet.getObject(fieldName)));
                }
                Assertions.assertEquals(user, instance);
            }
        }
    }

    private Object toJavaType(Object sqlType) {
        return switch (sqlType) {
            case Date sqlDate -> sqlDate.toLocalDate();
            case Timestamp sqlTimestamp -> sqlTimestamp.toLocalDateTime();
            default -> sqlType;
        };
    }

    private static String getSqlScriptBody(String path) {
        byte[] content;
        try (var is = ReflectionApiTest.class.getClassLoader().getResourceAsStream(path)) {
            if (is != null) {
                content = is.readAllBytes();
                return new String(content, StandardCharsets.UTF_8);
            }
            throw new IOException("Cannot find resource by path: " + path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveUser(User user, Connection connection) {
        var saveUserSql = """
                INSERT INTO %s
                (username, firstname, lastname, birth_date, age)
                VALUES (?, ?, ?, ?, ?);
                """.formatted(USER_TABLE);
        try (PreparedStatement preparedStatement = connection.prepareStatement(saveUserSql)) {
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getFirstname());
            preparedStatement.setString(3, user.getLastname());
            preparedStatement.setDate(4, Date.valueOf(user.getBirthDate()));
            preparedStatement.setInt(5, user.getAge());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
