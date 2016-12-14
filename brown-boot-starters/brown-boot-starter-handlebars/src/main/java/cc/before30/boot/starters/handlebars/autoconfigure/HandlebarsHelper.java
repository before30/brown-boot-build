package cc.before30.boot.starters.handlebars.autoconfigure;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * User: before30 
 * Date: 2016. 12. 14.
 * Time: 오전 11:33
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface HandlebarsHelper {
}
