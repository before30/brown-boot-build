package cc.before30.sample;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by before30 on 14/12/2016.
 */
@Slf4j
public class ConsoleHelloService implements HelloService {

    @Override
    public void sayHello() {
        log.info("this log from hello-starter : {}", "Hello from console");
    }

}
