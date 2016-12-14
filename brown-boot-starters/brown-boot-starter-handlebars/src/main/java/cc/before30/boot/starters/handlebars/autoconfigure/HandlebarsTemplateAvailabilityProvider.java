package cc.before30.boot.starters.handlebars.autoconfigure;

import org.springframework.boot.autoconfigure.template.TemplateAvailabilityProvider;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;

import static org.springframework.util.ClassUtils.isPresent;

/**
 * User: before30 
 * Date: 2016. 12. 14.
 * Time: 오전 11:35
 */
public class HandlebarsTemplateAvailabilityProvider implements TemplateAvailabilityProvider{
	@Override
	public boolean isTemplateAvailable(String view, Environment env, ClassLoader classLoader, ResourceLoader resourceLoader) {
		if (!isPresent("com.github.jknack.handlebars.Handlebars", classLoader)) {
			return false;
		}
		String prefix = env.getProperty("handlebars.prefix", HandlebarsProperties.DEFAULT_PREFIX);
		String suffix = env.getProperty("handlebars.suffix", HandlebarsProperties.DEFAULT_SUFFIX);

		return resourceLoader.getResource(prefix + view + suffix).exists();
	}
}
