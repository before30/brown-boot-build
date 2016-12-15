package cc.before30.boot.memcached;

import cc.before30.boot.memcached.autoconfigure.MemcachedAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Created by before30 on 15/12/2016.
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(MemcachedAutoConfiguration.class)
@Documented
public @interface EnableMemcached {
}
