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
public class TeamWriteDBService {
    @Autowired
    TeamRepository teamRepository;

    @Transactional(readOnly = false)
    public List<Team> findAll() {
        return teamRepository.findAll();
    }

    @Transactional(readOnly = false)
    public Team findByName(String name) {
        return teamRepository.findByName(name);
    }

    @Transactional(readOnly = false)
    public Team findOne(Long id) {
        return teamRepository.findOne(id);
    }

    @Transactional(readOnly = false)
    public void save(Team t) {
        teamRepository.save(t);
    }
}
