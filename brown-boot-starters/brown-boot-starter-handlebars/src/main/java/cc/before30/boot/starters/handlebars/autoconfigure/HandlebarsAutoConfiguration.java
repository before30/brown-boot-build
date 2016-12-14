package cc.before30.boot.starters.handlebars.autoconfigure;

import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.cache.GuavaTemplateCache;
import com.github.jknack.handlebars.cache.TemplateCache;
import com.github.jknack.handlebars.io.TemplateLoader;
import com.github.jknack.handlebars.io.TemplateSource;
import com.github.jknack.handlebars.springmvc.HandlebarsViewResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

import static com.google.common.cache.CacheBuilder.newBuilder;
/**
 * User: before30 
 * Date: 2016. 12. 14.
 * Time: 오전 9:49
 */

@ConditionalOnProperty(prefix = "handlebars", value = "enabled", havingValue = "true", matchIfMissing = true)
@Configuration
@EnableConfigurationProperties(HandlebarsProperties.class)
@ConditionalOnWebApplication
public class HandlebarsAutoConfiguration {

	@Configuration
	protected static class HandlebarsViewResolverConfiguration {
		@Autowired
		private HandlebarsProperties handlebars;

		@Bean
		public HandlebarsViewResolver handlebarsViewResolver() {
			HandlebarsViewResolver handlebarsViewResolver = new HandlebarsViewResolver();
			handlebars.applyToViewResolver(handlebarsViewResolver);
			return handlebarsViewResolver;
		}
	}

	@Configuration
	protected static class HandlebarsCacheConfiguration {
		@Autowired
		private HandlebarsViewResolver handlebarsViewResolver;

		@Autowired
		private TemplateCache templateCache;

		@PostConstruct
		public void setCachingStrategy() {
			if (handlebarsViewResolver.isCache()) {
				handlebarsViewResolver.getHandlebars().with(templateCache);
			}
		}

		@Bean
		@ConditionalOnMissingBean
		public TemplateCache templateCache() {
			return new GuavaTemplateCache(newBuilder().<TemplateSource, Template>build());
		}
	}

	@Configuration
	@ConditionalOnBean(TemplateLoader.class)
	protected static class HandlebarsTemplateLoaderConfiguration {
		@Autowired
		private HandlebarsViewResolver handlebarsViewResolver;

		@Autowired
		private TemplateLoader templateLoader;

		@PostConstruct
		public void setTemplateLoader() {
			handlebarsViewResolver.getHandlebars().with(templateLoader);
		}
	}

	@Configuration
	@ConditionalOnProperty("handlebars.prettyPrint")
	protected static class PrettyPrintConfiguration {
		@Autowired
		private HandlebarsViewResolver handlebarsViewResolver;

		@PostConstruct
		public void setPrettyPrint() {
			handlebarsViewResolver.getHandlebars().prettyPrint(true);
		}
	}

	@Configuration
	@ConditionalOnProperty("handlebars.infiniteLoops")
	protected static class InfiniteLoopsConfiguration {
		@Autowired
		private HandlebarsViewResolver handlebarsViewResolver;

		@PostConstruct
		public void setInfiniteLoops() {
			handlebarsViewResolver.getHandlebars().infiniteLoops(true);
		}
	}
}
