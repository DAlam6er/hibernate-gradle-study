package com.dmdev.v2;

import com.dmdev.v2.entity.Role;
import com.dmdev.v2.entity.User;
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
                    .username("admin-reserved@gmail.com")
                    .firstname("Ivan")
                    .lastname("Ivanov")
                    .birthDate(LocalDate.of(2000, 1, 19))
                    .age(24)
                    .role(Role.ADMIN)
                    .build();

            session.save(user);

            session.getTransaction().commit();
        }
    }
}
