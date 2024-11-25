package com.dmdev.v5;

import com.dmdev.v4.converter.BirthdayConverter;
import com.dmdev.v5.entity.Birthday;
import com.dmdev.v5.entity.Role;
import com.dmdev.v5.entity.User;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.time.LocalDate;

public class HibernateRunner {
    public static void main(String[] args) {
        saveEntity();
    }

    private static void saveEntity() {
        Configuration configuration = new Configuration();

        configuration.addAttributeConverter(new BirthdayConverter());
        // просто добавляем в List<BasicType> basicTypes новый тип
        configuration.registerTypeOverride(new JsonBinaryType());

        configuration.configure();
        try (SessionFactory sessionFactory = configuration.buildSessionFactory();
             Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            User user = User.builder()
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

            session.save(user);

            session.getTransaction().commit();
        }
    }
}
