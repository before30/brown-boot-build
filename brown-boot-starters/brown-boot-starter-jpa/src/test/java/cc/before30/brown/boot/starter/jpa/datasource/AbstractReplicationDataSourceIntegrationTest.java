package cc.before30.brown.boot.starter.jpa.datasource;

import cc.before30.brown.boot.starter.jpa.datasource.config.ReplicationDataSourceApplicationConfig;
import cc.before30.brown.boot.starter.jpa.datasource.jpa.UserOuterService;
import cc.before30.brown.boot.starter.jpa.datasource.jpa.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
/**
 * Created by before30 on 18/12/2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ReplicationDataSourceApplicationConfig.class)
@Slf4j
public abstract class AbstractReplicationDataSourceIntegrationTest {

    @Autowired
    private UserOuterService userOuterService;

    @Test
    public void findByIdRead() throws Exception {
        User user = userOuterService.findByIdRead(1);
        log.info("findByIdRead : {}", user);

        assertThat(user.getName()).as("readOnly=true Transaction").isEqualTo("read_1");
    }

    @Test
    public void findByIdWrite() throws Exception {
        User user = userOuterService.findByIdWrite(3);
        log.info("findByIdWrite : {}", user);

        assertThat(user.getName()).as("readOnly=false Transaction").isEqualTo("write_3");
    }

    @Test
    public void saveWrite() throws Exception {
        User newUser = new User();
        newUser.setName("New User");

        userOuterService.save(newUser);
        log.info("User saved : {}", newUser);

        User newUserFromRead = userOuterService.findByIdRead(newUser.getId());
        assertThat(newUserFromRead).as("New user is saved to write db. So read db must not have the user.").isNull();

        User newUserFromWrite = userOuterService.findByIdWrite(newUser.getId());
        assertThat(newUserFromWrite).as("New user is saved to write db. So write db must have the user.").isNotNull().isEqualTo(newUser);
    }

    @Test
    public void findByIdWriteAndInnerReadWithPropagationRequired() throws Exception {
        Map<String, User> users = userOuterService.findByIdWriteAndInnerReadWithPropagationRequired(1, 2, 3);

        assertThat(users.get("outerFirstUser").getName()).as("Outer first user id 1 from write db.").isEqualTo("write_1");
        assertThat(users.get("innerUser").getName()).as("Inner user id 2 from write db.").isEqualTo("write_2");
        assertThat(users.get("outerSecondUser").getName()).as("Outer second user id 3 from write db.").isEqualTo("write_3");
    }

//    @Test
//    public void findByIdWriteAndInnerReadWithPropagationRequiresNew() throws Exception {
//        Map<String, User> users = userOuterService.findByIdWriteAndInnerReadWithPropagationRequiresNew(3, 1, 3);
//
//        assertThat(users.get("outerFirstUser").getName()).as("Outer first user id 1 from write db.").isEqualTo("write_3");
//        assertThat(users.get("innerUser").getName()).as("Inner user id 2 from write db.").isEqualTo("read_1");
//        assertThat(users.get("outerSecondUser").getName()).as("Outer second user id 3 from write db.").isEqualTo("write_3");
//    }

    @Test
    public void findByIdWriteAndInnerReadWithPoropagationMandatory() throws Exception {
        Map<String, User> users = userOuterService.findByIdWriteAndInnerReadWithPoropagationMandatory(2, 3, 1);

        assertThat(users.get("outerFirstUser").getName()).as("Outer first user id 1 from write db.").isEqualTo("write_2");
        assertThat(users.get("innerUser").getName()).as("Inner user id 2 from write db.").isEqualTo("write_3");
        assertThat(users.get("outerSecondUser").getName()).as("Outer second user id 3 from write db.").isEqualTo("write_1");
    }
}
