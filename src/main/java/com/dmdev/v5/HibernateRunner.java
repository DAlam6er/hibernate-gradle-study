package com.dmdev.v5;

import com.dmdev.v2.entity.Role;
import com.dmdev.v3.entity.Birthday;
import com.dmdev.v4.converter.BirthdayConverterAutoApply;
import com.dmdev.v5.entity.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.time.LocalDate;

public class HibernateRunner {
    public static void main(String[] args) throws JsonProcessingException {
        User daddy = User.builder()
                .username("alex@gmail.com")
                .firstname("Alexey")
                .lastname("Zvyagintsev")
                .birthDate(new Birthday(LocalDate.of(1993, 11, 5)))
                .info("""
                        {
                            "parent-username": "nick@gmail.com",
                            "child-username": "alice@gmail.com"
                        }
                        """)
                .role(Role.USER)
                .build();

        deleteEntity(daddy); // tear down
        saveEntity(daddy);

        User daddyWithFixedBirthday = daddy.withBirthDate(new Birthday(LocalDate.of(1991, 1, 1)));

        updateEntity(daddyWithFixedBirthday);
        User daughter = User.builder()
                .username("alice@gmail.com")
                .firstname("Alice")
                .lastname("Zvyagintseva")
                .birthDate(new Birthday(LocalDate.of(2014, 11, 5)))
                .info("""
                        {
                            "parent-username": "alex@gmail.com"
                        }
                        """)
                .role(Role.USER)
                .build();
        saveOrUpdateEntity(daughter);

        String grandFatherId = new ObjectMapper().readTree(daddy.getInfo()).get("parent-username").asText();
        User grandfather = new HibernateRunner().getEntity(grandFatherId);
        if (grandfather == null) {
            System.out.printf("Grandfather with id: %s not found\n", grandFatherId);
        } else {
            System.out.println("Grandfather: " + grandfather);
        }
    }

    private static void saveEntity(User user) {
        Configuration configuration = new Configuration();

        configuration.addAttributeConverter(new BirthdayConverterAutoApply());
        // просто добавляем в List<BasicType> basicTypes новый тип
        configuration.registerTypeOverride(new JsonBinaryType());

        configuration.configure();
        try (SessionFactory sessionFactory = configuration.buildSessionFactory();
             Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            session.save(user);

            session.getTransaction().commit();
        }
    }


    /**
     * Если запись не будет найдена, кинет исключение
     * org.hibernate.StaleStateException: Batch update returned unexpected row count from update [0];
     * actual row count: 0; expected: 1; statement executed:
     * update public.users_v4 set birth_date=?, firstname=?, info=?, lastname=?, role=? where username=?
     *
     * @param user пользователь, чьи поля необходимо обновить
     * @throws org.hibernate.StaleStateException если запись не существует в БД
     */
    private static void updateEntity(User user) {
        Configuration configuration = new Configuration();

        configuration.addAttributeConverter(new BirthdayConverterAutoApply());
        configuration.registerTypeOverride(new JsonBinaryType());

        configuration.configure();
        try (SessionFactory sessionFactory = configuration.buildSessionFactory();
             Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            session.update(user); // у Hibernate отложенная отправка запросов, т.о. при выходе из этого метода обращения в БД не будет

            session.getTransaction().commit(); // запрос выполнится при коммите транзакции или закрытии сессии
        }
    }

    /**
     * Если запись не будет найдена, она будет добавлена в БД
     *
     * @param user сохраняемый или обновляемый пользователь
     */
    private static void saveOrUpdateEntity(User user) {
        Configuration configuration = new Configuration();

        configuration.addAttributeConverter(new BirthdayConverterAutoApply());
        configuration.registerTypeOverride(new JsonBinaryType());

        configuration.configure();
        try (SessionFactory sessionFactory = configuration.buildSessionFactory();
             Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            session.saveOrUpdate(user); // при выходе из этого метода будет обращение в БД с SELECT (но не INSERT или UPDATE)

            session.getTransaction().commit(); // отложенный запрос INSERT или UPDATE выполнится при коммите транзакции или закрытии сессии
        }
    }

    /**
     * Удаляет пользователя по его идентификатору.
     * Если запись не будет найдена, никаких исключений не бросается
     *
     * @param user пользователь, подлежащий удалению
     */
    private static void deleteEntity(User user) {
        Configuration configuration = new Configuration();

        configuration.addAttributeConverter(new BirthdayConverterAutoApply());
        configuration.registerTypeOverride(new JsonBinaryType());

        configuration.configure();
        try (SessionFactory sessionFactory = configuration.buildSessionFactory();
             Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            session.delete(user); // при выходе из этого метода будет обращение в БД с SELECT (но не DELETE)

            session.getTransaction().commit(); // отложенный запрос на удаление выполнится при коммите транзакции или закрытии сессии
        }
    }

    private User getEntity(String id) {
        Configuration configuration = new Configuration();

        configuration.addAttributeConverter(new BirthdayConverterAutoApply());
        configuration.registerTypeOverride(new JsonBinaryType());

        configuration.configure();
        try (SessionFactory sessionFactory = configuration.buildSessionFactory();
             Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            // Уникальным ключом для сессии - это класс сущности+идентификатор
            User user = session.get(User.class, id); // при выходе из этого метода будет обращение в БД с SELECT

            session.getTransaction().commit();
            return user;
        }
    }
}
