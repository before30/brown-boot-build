package cc.before30;

import cc.before30.boot.memcached.EnableMemcached;
import lombok.extern.slf4j.Slf4j;
import net.spy.memcached.MemcachedClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@SpringBootApplication
@EnableMemcached
@Slf4j
public class BrownBootSampleMemcachedApplication {

	@Autowired
	MemcachedClient memcachedClient;

	@PostConstruct
	public void init() throws InterruptedException, ExecutionException, TimeoutException {
		log.info("start....");
		memcachedClient.add("test",100, "test1");
		log.info(memcachedClient.get("test").toString());

	}
	public static void main(String[] args) {
		SpringApplication.run(BrownBootSampleMemcachedApplication.class, args);
	}
}
