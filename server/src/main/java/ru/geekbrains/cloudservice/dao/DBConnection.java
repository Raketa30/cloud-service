package ru.geekbrains.cloudservice.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import ru.geekbrains.cloudservice.model.User;

import javax.persistence.Query;
import java.util.Optional;

public class DBConnection {
    private SessionFactory sessionFactory;

    public DBConnection() {
        sessionFactory = HibernateUtil.getSessionFactory();
    }

    public Optional<User> findUserByUsernameAndPassword(User user) {
        Session session = sessionFactory.openSession();
        session.get(User.class, 1L);

        Query query = session.createQuery("from User as u where u.username = :username and u.password = :password");
        query.setParameter("username", user.getUsername());
        query.setParameter("password", user.getPassword());

        User result = (User) query.getSingleResult();
        session.close();
        System.out.println(result);
        return Optional.of(result);
    }

    public boolean findUserByUsername(String username) {
        Session session = sessionFactory.openSession();
        session.get(User.class, 1L);

        Query query = session.createQuery("from User as u where u.username = :username");
        query.setParameter("username", username);


        User result = (User) query.getSingleResult();
        session.close();
        System.out.println(result);
        return result == null;
    }

    public void registerNewUser(User user) {
        Session session = sessionFactory.openSession();
        session.get(User.class, 1L);
        Transaction tx = session.beginTransaction();
        session.save(user);
        tx.commit();
    }
}
