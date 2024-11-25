package com.dmdev.v4;

import com.dmdev.v4.converter.BirthdayConverter;
import com.dmdev.v4.entity.Birthday;
import com.dmdev.v4.entity.Role;
import com.dmdev.v4.entity.User;
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

        // autoApply можно опустить (default = false), но тогда нужна аннотация @Converter(autoApply = true) над BirthdayConverter
        // configuration.addAttributeConverter(new BirthdayConverter(), true);
        configuration.addAttributeConverter(new BirthdayConverter()); // вариант 2 указания конвертера

        configuration.configure();
        try (SessionFactory sessionFactory = configuration.buildSessionFactory();
             Session session = sessionFactory.openSession())
        {
            session.beginTransaction();

            User user = User.builder()
                    .username("nick@gmail.com")
                    .firstname("Nickolay")
                    .lastname("Zvyagintsev")
                    .birthDate(new Birthday(LocalDate.of(1974, 11, 5)))
                    .role(Role.USER)
                    .build();

            session.save(user);

            session.getTransaction().commit();
        }
    }
}
