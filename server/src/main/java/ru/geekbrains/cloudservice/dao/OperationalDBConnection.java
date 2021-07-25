package ru.geekbrains.cloudservice.dao;

import org.hibernate.SessionFactory;

public class OperationalDBConnection {
    private SessionFactory sessionFactory;

    public OperationalDBConnection(SessionFactory sessionFactory) {
        this.sessionFactory = HibernateUtil.getSessionFactory();
    }
}
