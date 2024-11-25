package com.dmdev.v1;

import com.dmdev.v1.entity.User;
import com.dmdev.v1.entity.UserNotInConfigFileAndWithoutColumnAnnotation;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.hibernate.cfg.Configuration;

import java.time.LocalDate;

public class HibernateRunner {
    private static final User user = User.builder()
            .username("admin-reserved@gmail.com")
            .firstname("Ivan")
            .lastname("Ivanov")
            .birthDate(LocalDate.of(2000, 1, 19))
            .age(24)
            .build();

    public static void main(String[] args) {
        saveEntityUsingStaticAddForTracking();
    }

    /**
     * Статическая регистрация сущности в Session Factory для отслеживания последней
     * путём добавления в теге "mapping" аттрибута "class" файла hibernate.cfg.xml
     */
    private static void saveEntityUsingStaticAddForTracking() {
        Configuration configuration = new Configuration();
        saveEntityUsingColumnAnnotation(configuration);
    }

    private static void saveEntityUsingColumnAnnotation(Configuration configuration) {
        // Используем аннотацию @Column на нужном поле в com.dmdev.v1.entity.User

        saveEntity(configuration);
    }

    /**
     * Динамическая регистрация сущности в Session Factory для отслеживания последней
     */
    private static void saveEntityUsingDynamicAddForTracking() {
        Configuration configuration = new Configuration();
        configuration.addAnnotatedClass(UserNotInConfigFileAndWithoutColumnAnnotation.class);

        saveEntityUsingSettingGlobalNamingStrategy(configuration);
    }

    private static void saveEntityUsingSettingGlobalNamingStrategy(Configuration configuration) {
        // Устанавливаем стратегию именования колонок таблицы на основании полей сущности в Java
        setGlobalNamingStrategy(configuration);

        saveEntity(configuration);
    }

    private static void saveEntity(Configuration configuration) {
        configuration.configure();
        try (SessionFactory sessionFactory = configuration.buildSessionFactory();
             Session session = sessionFactory.openSession())
        {
            session.beginTransaction(); // в Hibernate нет autocommit mode как в JDBC
            session.save(user);
            session.getTransaction().commit();
        }
    }

    /**
     * Устанавливаем глобальную стратегию именования колонок таблицы на основании полей сущности в Java
     * @param configuration - объект Configuration
     */
    private static void setGlobalNamingStrategy(Configuration configuration) {
        configuration.setPhysicalNamingStrategy(new CamelCaseToUnderscoresNamingStrategy());
    }
}
