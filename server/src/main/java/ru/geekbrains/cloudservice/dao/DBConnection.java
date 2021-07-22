package ru.geekbrains.cloudservice.dao;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import ru.geekbrains.cloudservice.model.User;

import javax.persistence.Query;
import java.util.Optional;

@Slf4j
public class DBConnection {
    private SessionFactory sessionFactory;

    public DBConnection() {
        sessionFactory = HibernateUtil.getSessionFactory();
    }

    public Optional<User> findUserByUsernameAndPassword(User user) {
        try {
            Session session = sessionFactory.openSession();
            session.get(User.class, 1L);

            Query query = session.createQuery("from User as u where u.username = :username and u.password = :password");
            query.setParameter("username", user.getUsername());
            query.setParameter("password", user.getPassword());

            User result = (User) query.getSingleResult();
            session.close();
            System.out.println(result);
            return Optional.of(result);
        } catch (Exception e) {
            log.info("find userByUsernameAndPassword ex");
        }

        return Optional.empty();

    }

    public Optional<User> findUserByUsername(String username) {
        try {
            Session session = sessionFactory.openSession();
            session.get(User.class, 1L);

            Query query = session.createQuery("from User as u where u.username = :username");
            query.setParameter("username", username);

            Optional<User> optionalUser = Optional.of((User) query.getSingleResult());
            session.close();
            return optionalUser;
        } catch (Exception e) {
            log.info("find userByUsername excp");
        }

        return Optional.empty();
    }

    public void registerNewUser(User user) {
        Session session = sessionFactory.openSession();
        session.get(User.class, 1L);
        Transaction tx = session.beginTransaction();
        session.save(user);
        tx.commit();
    }
}
