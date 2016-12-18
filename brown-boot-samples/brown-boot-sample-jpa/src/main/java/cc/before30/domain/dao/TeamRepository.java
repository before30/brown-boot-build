package cc.before30.domain.dao;

import cc.before30.domain.Team;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by before30 on 18/12/2016.
 */

@Repository
public interface TeamRepository extends CrudRepository<Team, Long> {
    List<Team> findAll();

    Team findByName(String name);
}
