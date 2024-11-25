package com.dmdev;

import com.dmdev.entity.AgeType;
import com.dmdev.entity.PersonalInfo;
import com.dmdev.entity.Role;
import com.dmdev.entity.User;
import com.dmdev.util.HibernateUtil;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.time.LocalDate;

import static java.util.Optional.ofNullable;

@Slf4j
public class HibernateRunner {
//     в %c в appender log4j.xml будет подставлено имя класса
//     вместо того, чтобы писать эту строку из класса в класс, используют аннотацию lombok @Slf4j
//    private static final Logger log = LoggerFactory.getLogger(HibernateRunner.class);

    public static void main(String[] args) {
        demoSessionFactoryCongirure();
//        demoCrud();
//        demoEntityLifeCycle();
//        demoCompositeKey();
    }

    private static void demoSessionFactoryCongirure() {
//        BlockingDeque<Connection> pool = null;
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

    private static void demoCompositeKey() {
        var user = User.builder()
                .username("petr@gmail.com")
                .personalInfo(PersonalInfo.builder()
                        .lastname("Petrov")
                        .firstname("Petr")
                        .birthDate(new AgeType(LocalDate.of(2000, 1, 2)))
                        .build())
                .build();

        log.info("User entity is in transient state, object: {}", user);

        // SessionFactory - аналог ConnectionPool в JDBC
        try (var sessionFactory = HibernateUtil.buildSessionFactory()) {
            // Session - аналог Connection в JDBC
            try (Session session = sessionFactory.openSession()) {
                session.beginTransaction();
                session.saveOrUpdate(user);
                log.trace("User entity is in persistent state: {}, session {}", user, session);

                session.getTransaction().commit();
            }
            try (Session session = sessionFactory.openSession()) {
                PersonalInfo key = PersonalInfo.builder()
                        .lastname("Petrov")
                        .firstname("Petr")
                        .birthDate(new AgeType(LocalDate.of(2000, 1, 2)))
                        .build();

                var user2 = session.get(User.class, key);
                System.out.println(user2);
            }
        }
    }

    public static void demoCrud() {
        try (var sessionFactory = HibernateUtil.buildSessionFactory();
             var session = sessionFactory.openSession()) {
            session.beginTransaction();
            var user = User.builder()
                    .username("ivan@gmail.com")
                    .personalInfo(PersonalInfo.builder()
                            .firstname("Ivan")
                            .lastname("Ivanov")
                            .birthDate(new AgeType(LocalDate.of(2000, 1, 19)))
                            .build())
                    .role(Role.ADMIN)
                    .info("""
                            {
                                "name": "Ivan",
                                "id": 25
                            }
                            """)
                    .build();

//            session.save(user); // deprecated method since Hibernate 6.0
//            session.update(user);
//            session.saveOrUpdate(user);
//            session.delete(user);

            var maybeUser = ofNullable(session.get(User.class, "ivan@gmail.com"));
            maybeUser.ifPresent(existedUser -> existedUser.getPersonalInfo().setLastname("Ivanov"));
            System.out.println("Session is dirty: " + session.isDirty());
            session.flush();
            session.getTransaction().commit();
        }
    }

    public static void demoEntityLifeCycle() {
        // Сущность в Transient по отношению к session1 и session2
        var user = User.builder()
                .username("petr@gmail.com")
                .personalInfo(PersonalInfo.builder()
                        .lastname("Petrov")
                        .firstname("Petr")
                        .birthDate(new AgeType(LocalDate.of(2000, 1, 2)))
                        .build())
                .build();

        // противоречит правилам хорошего тона
//        log.info("User entity is in transient state, object: " + user);
        log.info("User entity is in transient state, object: {}", user);

        try (var sessionFactory = HibernateUtil.buildSessionFactory()) {
            var session1 = sessionFactory.openSession();
            try (session1) {
                var transaction = session1.beginTransaction();
                log.trace("Transaction is created, {}", transaction);

                // Сущность в Transient по отношению к session1
                // Сущность в Transient по отношению к session2
                session1.saveOrUpdate(user);
                log.trace("User is in persistent state: {}, session {}", user, session1);
                // Сущность в Persistent по отношению к session1
                // Сущность в Transient по отношению к session2

                session1.getTransaction().commit();
            }
            log.warn("User is in detached state: {}, session is closed {}", user, session1);
            // Сущность в Detached по отношению к session1 (точнее говоря session1 уже не существует)
            // Сущность в Transient по отношению к session2

/*
            try (var session2 = sessionFactory.openSession()) {
                // delete
//                session2.beginTransaction();
//                session2.delete(user); // 1) get() 2) DELETE
//                session2.getTransaction().commit();
//              Сущность в Removed по отношению к session2

                // refresh/merge
                session2.beginTransaction();
                var personalInfo = user.getPersonalInfo();
                personalInfo.setFirstname("Sveta");
                // Сущность в Transient по отношению к session2
                // т.к. всё ещё не проассоциирована с session2

                // отправляем запрос в БД, и накладываем все изменения из БД
                // на сущность user
                session2.refresh(user);
                // Сущность в Persistence по отношению к session2 (в PersistenceContext добавлен user)
                // сущность user получила значение firstName из БД - Ivan
                // под капотом:
                var freshUser = session2.get(User.class, user.getId());

                personalInfo.setLastname(freshUser.getPersonalInfo().getLastname());
                personalInfo.setFirstname(freshUser.getPersonalInfo().getFirstname());
                session2.getTransaction().commit();
            }
 */
        } catch (Exception exception) {
            log.error("Exception occurred", exception);
            throw exception;
        }
    }
}