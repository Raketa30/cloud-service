package ru.geekbrains.cloudservice.dao;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import ru.geekbrains.cloudservice.model.User;

import javax.persistence.Query;
import java.util.Optional;

@Slf4j
public class UserDBConnection {
    private SessionFactory sessionFactory;

    public UserDBConnection() {
        sessionFactory = HibernateUtil.getSessionFactory();
    }

    public Optional<User> findUserByUsernameAndPassword(User user) {

        User result;
        try (Session session = sessionFactory.openSession()) {
            session.get(User.class, 1L);

            Query query = session.createQuery("from User as u where u.username = :username and u.password = :password");
            query.setParameter("username", user.getUsername());
            query.setParameter("password", user.getPassword());

            result = (User) query.getSingleResult();

            log.debug(result.toString());
            return Optional.of(result);
        } catch (Exception e) {
            log.warn("find userByUsernameAndPassword ex");
            return Optional.empty();
        }


    }

    public Optional<User> findUserByUsername(String username) {
        Optional<User> optionalUser;
        try (Session session = sessionFactory.openSession()) {
            session.get(User.class, 1L);

            Query query = session.createQuery("from User as u where u.username = :username");
            query.setParameter("username", username);

            optionalUser = Optional.of((User) query.getSingleResult());
            return optionalUser;
        } catch (Exception e) {
            log.warn("find userByUsername exception {}", e.getMessage());
            return Optional.empty();
        }
    }

    public void registerNewUser(User user) {
        Transaction tx;
        try (Session session = sessionFactory.openSession()) {
            session.get(User.class, 1L);
            tx = session.beginTransaction();
            session.save(user);
            log.debug("user saved : {}", user);
            tx.commit();
        }
    }
}
