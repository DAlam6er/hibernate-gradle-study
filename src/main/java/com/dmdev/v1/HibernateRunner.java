package com.dmdev.v1;

import com.dmdev.v1.entity.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.time.LocalDate;

public class HibernateRunner {
    public static void main(String[] args) {
//        demoSessionFactoryConfigure();
        saveEntity();
    }

    private static void demoSessionFactoryConfigure() {
//        BlockingDeque<Connection> pool = null; // 10...20
//        Connection conn = pool.take();
//        SessionFactory

//        Connection conn = DriverManager
//                .getConnection("db.url", "db.username", "db.password");
//        Session

        Configuration configuration = new Configuration();
        configuration.configure(); // default path is "./hibernate.cfg.xml"
        try (SessionFactory sessionFactory = configuration.buildSessionFactory();
             Session session = sessionFactory.openSession()) {
            System.out.println("OK");
        }
    }

    private static void saveEntity() {
        Configuration configuration = new Configuration();
        // Добавляем сущность для отслеживания
        // Вариант 1 - динамическое добавление
//        configuration.addAnnotatedClass(User.class);
        // Вариант 2 - статическое добавление в теге mapping аттрибута class файла hibernate.cfg.xml

        // Устанавливаем стратегию именования колонок таблицы на основании полей сущности в Java
        // Вариант 1 - глобальная установка
//        configuration.setPhysicalNamingStrategy(new CamelCaseToUnderscoresNamingStrategy());
        // Вариант 2 - использование аннотации @Column на нужном поле

        configuration.configure();
        try (SessionFactory sessionFactory = configuration.buildSessionFactory();
             Session session = sessionFactory.openSession())
        {
            // в Hibernate нет autocommit mode как в JDBC
            session.beginTransaction();

            User user = User.builder()
                    .username("ivan@gmail.com")
                    .firstname("Ivan")
                    .lastname("Ivanov")
                    .birthDate(LocalDate.of(2000, 1, 19))
                    .age(24)
                    .build();

            session.save(user);

            session.getTransaction().commit();
        }
    }
}
