package ru.geekbrains.cloudservice.dao;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import ru.geekbrains.cloudservice.model.FileInfo;

import javax.persistence.Query;
import java.util.List;
import java.util.Optional;

@Slf4j
public class OperationalDBConnection {
    private SessionFactory sessionFactory;

    public OperationalDBConnection() {
        this.sessionFactory = HibernateUtil.getSessionFactory();
    }

    public Optional<FileInfo> findFileByRelativePath(String filePath) {
        try {
            Session session = sessionFactory.openSession();
            session.get(FileInfo.class, 1L);
            Query query = session.createQuery("from FileInfo as f where f.filePath = :filePath");
            query.setParameter("filePath", filePath);

            FileInfo result = (FileInfo) query.getSingleResult();
            return Optional.of(result);

        } catch (Exception e) {
            log.warn("file not found");
        }
        return Optional.empty();
    }


    public Optional<List<FileInfo>> findFilesByParentPath(String parentPath) {
        try {
            Session session = sessionFactory.openSession();
            session.get(FileInfo.class, 1L);
            Query query = session.createQuery("from FileInfo as f where f.parentPath = :parentPath");
            query.setParameter("parentPath", parentPath);

            List<FileInfo> fileInfoList = query.getResultList();
            return Optional.of(fileInfoList);

        } catch (Exception e) {
            log.warn("file not found");
        }
        return Optional.empty();
    }
}
