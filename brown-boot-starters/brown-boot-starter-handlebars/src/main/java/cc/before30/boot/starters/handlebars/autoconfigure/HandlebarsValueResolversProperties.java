package cc.before30.boot.starters.handlebars.autoconfigure;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * User: before30 
 * Date: 2016. 12. 14.
 * Time: 오전 9:52
 */

@ConfigurationProperties(prefix = "handlebars.resolver")
@Setter
@Getter
public class HandlebarsValueResolversProperties {
	private boolean map = true;
	private boolean javaBean = true;
	private boolean field = false;
	private boolean method = false;

}
