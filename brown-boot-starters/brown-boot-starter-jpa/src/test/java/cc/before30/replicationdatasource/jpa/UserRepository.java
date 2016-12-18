package cc.before30.replicationdatasource.jpa;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created by before30 on 18/12/2016.
 */

@Repository
public class UserRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public User findById(Integer id) {
        return entityManager.find(User.class, id);
    }

    public void save(User user) {
        entityManager.persist(user);
    }
}
