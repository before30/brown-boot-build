package cc.before30;

import cc.before30.brown.boot.starter.jpa.datasource.LazyReplicationConnectionDataSourceProxy;
import cc.before30.domain.Team;
import cc.before30.domain.dao.TeamReadDBService;
import cc.before30.domain.dao.TeamWriteDBService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.List;

@SpringBootApplication
@Slf4j
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
public class BrownBootSampleJpaApplication {

	@Bean
	public DataSource writeDataSource() {
		EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder()
				.setName("writeDb")
				.setType(EmbeddedDatabaseType.H2)
				.setScriptEncoding("UTF-8")
				.addScript("classpath:/readdb.sql");

		return builder.build();
	}

	@Bean
	public DataSource readDataSource() {
		EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder()
				.setName("readDb")
				.setType(EmbeddedDatabaseType.H2)
				.setScriptEncoding("UTF-8")
				.addScript("classpath:/readdb.sql");
		return builder.build();
	}

	@Bean
	public DataSource dataSource(
			@Qualifier("writeDataSource") DataSource writeDataSource,
			@Qualifier("readDataSource") DataSource readDataSource
	) {
		return new LazyReplicationConnectionDataSourceProxy(writeDataSource, readDataSource);
	}

	public static void main(String[] args) {
		SpringApplication.run(BrownBootSampleJpaApplication.class, args);
	}

}

@RestController
class TeamController {
	@Autowired
	TeamReadDBService teamReadDBService;

	@Autowired
	TeamWriteDBService teamWriteDBService;

	@PostConstruct
	public void init() {
		for (int i=0; i<10; i++) {
			teamReadDBService.save(new Team("read__" + i));
			teamWriteDBService.save(new Team("write__" + i));
		}
	}


	@RequestMapping("/teams/{readOnly}")
	public List<Team> teams(@PathVariable Boolean readOnly) {
		return readOnly ? teamReadDBService.findAll() : teamWriteDBService.findAll();
	}

	@RequestMapping("/team/{name}/{readOnly}")
	public Team team(@PathVariable("name") String name, @PathVariable("readOnly") Boolean readOnly) {
		return readOnly ? teamReadDBService.findByName(name) : teamWriteDBService.findByName(name);
	}
}
