package com.dmdev.v4;

import com.dmdev.v3.entity.Birthday;
import com.dmdev.v2.entity.Role;
import com.dmdev.v4.converter.BirthdayConverter;
import com.dmdev.v4.converter.BirthdayConverterAutoApply;
import com.dmdev.v4.entity.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.time.LocalDate;

/**
 * Вариант 2 указания конвертера: автоматически использовать его в любой из сущностей,
 * без необходимости проставлять аннотацию @Convert(converter = BirthdayConverter.class)
 */
public class HibernateRunner {
    public static void main(String[] args) {
        // saveEntityAutoApplyInMethod();
        saveEntityAutoApplyInAnnotation();
    }

    /**
     * Регистрируем конвертер, используя класс Configuration.
     * Указываем в методе autoApply = true.
     * В противном случае ловим org.hibernate.MappingException:
     * Could not determine type for: com.dmdev.v3.entity.Birthday,
     * at table: users_v3, for columns: [org.hibernate.mapping.Column(birth_date)]
     */
    private static void saveEntityAutoApplyInMethod() {
        Configuration configuration = new Configuration();

        configuration.addAttributeConverter(new BirthdayConverter(), true);
        saveEntity(configuration);
    }

    /**
     * Используем дефолтное значение autoApply = false в методе addAttributeConverter().
     * Используем аннотацию @Converter(autoApply = true) над классом конвертера, добавляемого в этом методе
     */
    private static void saveEntityAutoApplyInAnnotation() {
        Configuration configuration = new Configuration();
        configuration.addAttributeConverter(new BirthdayConverterAutoApply());
        saveEntity(configuration);
    }

    private static void saveEntity(Configuration configuration) {
        configuration.configure();
        try (SessionFactory sessionFactory = configuration.buildSessionFactory();
             Session session = sessionFactory.openSession()) {
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
