package cc.before30.domain.dao;

import cc.before30.domain.Team;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by before30 on 19/12/2016.
 */

@Service
public class TeamReadDBService {
    @Autowired
    TeamRepository teamRepository;

    @Transactional(readOnly = true)
    public List<Team> findAll() {
        return teamRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Team findByName(String name) {
        return teamRepository.findByName(name);
    }

    @Transactional(readOnly = true)
    public Team findOne(Long id) {
        return teamRepository.findOne(id);
    }

    @Transactional(readOnly = true)
    public void save(Team t) {
        teamRepository.save(t);
    }

}
