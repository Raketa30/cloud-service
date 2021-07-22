package ru.geekbrains.cloudservice.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
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

        User result = (User)query.getSingleResult();
        session.close();
        System.out.println(result);
        return Optional.of(result);
    }
}
