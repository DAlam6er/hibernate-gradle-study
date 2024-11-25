package com.dmdev;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * 02 Для чего нужен Configuration, как работает Session Factory и Session
 */
public class ConfigurationRunner {
    public static void main(String[] args) {
        /*
        Configuration содержит всё что нужно для создания session factory:
        - стратегии именования
        - какие типы поддерживает
        - именованные запросы
        - процедуры
        - sql-функции
        - свойства, используемые при создании `SessionFactory`
        - всё, что связано с метаинформацией
        */
        Configuration configuration = new Configuration();
        configuration.configure(); // DEFAULT_CFG_RESOURCE_NAME = "hibernate.cfg.xml"
        try (SessionFactory sessionFactory = configuration.buildSessionFactory();
             Session session = sessionFactory.openSession()) {
            System.out.println("Session properties:\n\t" + getSessionPropertiesAsString(session.getProperties()));
        }
    }

    private static String getSessionPropertiesAsString(Map<String, Object> properties) {
        return properties.entrySet()
                .stream()
                .map(entry -> entry.getKey() + " = " + entry.getValue())
                .collect(Collectors.joining("\n\t"));
    }
}
