package com.dmdev.v3;

import com.dmdev.v3.entity.Birthday;
import com.dmdev.v3.entity.Role;
import com.dmdev.v3.entity.User;
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

        configuration.configure();
        try (SessionFactory sessionFactory = configuration.buildSessionFactory();
             Session session = sessionFactory.openSession())
        {
            session.beginTransaction();

            User user = User.builder()
                    .username("alice@gmail.com")
                    .firstname("Alice")
                    .lastname("Liddell")
                    .birthDate(new Birthday(LocalDate.of(2014, 11, 5)))
                    .role(Role.USER)
                    .build();

            session.save(user);

            session.getTransaction().commit();
        }
    }
}
