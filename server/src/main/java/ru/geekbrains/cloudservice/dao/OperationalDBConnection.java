package ru.geekbrains.cloudservice.dao;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import ru.geekbrains.cloudservice.dto.FileInfoTo;

import javax.persistence.Query;
import java.util.List;
import java.util.Optional;

@Slf4j
public class OperationalDBConnection {
    private final SessionFactory sessionFactory;

    public OperationalDBConnection() {
        this.sessionFactory = HibernateUtil.getSessionFactory();
    }

    public Optional<FileInfoTo> findFileByRelativePath(String filePath) {
        Query query;
        try (Session session = sessionFactory.openSession()) {
            session.get(FileInfoTo.class, 1L);
            query = session.createQuery("from FileInfoTo as f where f.filePath = :filePath");
            query.setParameter("filePath", filePath);

            FileInfoTo result = (FileInfoTo) query.getSingleResult();
            return Optional.of(result);

        } catch (Exception e) {
            log.warn("file not found");
        }
        return Optional.empty();
    }

    public Optional<List<FileInfoTo>> findFilesByParentPath(String parentPath) {
        Query query;
        try (Session session = sessionFactory.openSession()) {
            session.get(FileInfoTo.class, 1L);
            query = session.createQuery("from FileInfoTo as f where f.parentPath = :parentPath");
            query.setParameter("parentPath", parentPath);

            List<FileInfoTo> fileInfoToList = query.getResultList();
            return Optional.of(fileInfoToList);

        } catch (Exception e) {
            log.warn("file not found");
        }
        return Optional.empty();
    }

    public void saveFileInfo(FileInfoTo fileInfoTo) {
        Transaction tx;
        try (Session session = sessionFactory.openSession()) {
            session.get(FileInfoTo.class, 1L);
            tx = session.beginTransaction();
            session.save(fileInfoTo);
            tx.commit();
        }
    }

}
