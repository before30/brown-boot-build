package cc.before30.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by before30 on 18/12/2016.
 */
@Entity
@Setter
@Getter
@EqualsAndHashCode
@ToString
public class Team {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    public Team() {
    }

    public Team(String name) {
        this.name = name;
    }
}
