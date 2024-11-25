package com.dmdev.util;

import com.dmdev.converter.AgeConverter;
import com.dmdev.entity.User;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import lombok.experimental.UtilityClass;
import org.hibernate.SessionFactory;
import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.hibernate.cfg.Configuration;

@UtilityClass
public class HibernateUtil {
    // аналог BlockingDeque<Connection> pool в JDBC
    public static SessionFactory buildSessionFactory() {
        Configuration configuration = new Configuration();
        // маппинг имен типа birthDate в имена типа birth_date
        configuration.setPhysicalNamingStrategy(new CamelCaseToUnderscoresNamingStrategy());
        // делаем так, чтобы Hibernate отслеживал сущность, другой вариант описан в hibernate.cfg.xml
        configuration.addAnnotatedClass(User.class);
        configuration.addAttributeConverter(AgeConverter.class);
        configuration.registerTypeOverride(new JsonBinaryType());
        configuration.configure();

        return configuration.buildSessionFactory();
    }
}
