package cc.before30;

import cc.before30.sample.HelloService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.annotation.PostConstruct;

@SpringBootApplication
@Slf4j
public class BrownBootSampleHelloApplication {

	@Autowired
	HelloService helloService;

	@PostConstruct
	public void init() {
		helloService.sayHello();
	}

	public static void main(String[] args) {
		SpringApplication.run(BrownBootSampleHelloApplication.class, args);
	}

	@Bean
	public HelloService customHelloServices() {
		return () -> log.info("Hello from Logger");
	}
}
