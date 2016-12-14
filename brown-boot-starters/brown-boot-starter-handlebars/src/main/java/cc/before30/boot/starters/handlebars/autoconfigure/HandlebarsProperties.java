package cc.before30.boot.starters.handlebars.autoconfigure;

import com.github.jknack.handlebars.ValueResolver;
import com.github.jknack.handlebars.context.FieldValueResolver;
import com.github.jknack.handlebars.context.JavaBeanValueResolver;
import com.github.jknack.handlebars.context.MapValueResolver;
import com.github.jknack.handlebars.context.MethodValueResolver;
import com.github.jknack.handlebars.springmvc.HandlebarsViewResolver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.template.AbstractTemplateViewResolverProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.util.Assert.isInstanceOf;
/**
 * User: before30 
 * Date: 2016. 12. 14.
 * Time: 오전 9:51
 */

@ConfigurationProperties(prefix = "handlebars")
@EnableConfigurationProperties(HandlebarsValueResolversProperties.class)
public class HandlebarsProperties extends AbstractTemplateViewResolverProperties {

	static final String DEFAULT_PREFIX = "classpath:templates/";
	static final String DEFAULT_SUFFIX = ".hbs";

	private Boolean registerMessageHelper = true;
	private Boolean failOnMissingFile = false;

	private final HandlebarsValueResolversProperties valueResolversProperties;

	@Autowired
	protected HandlebarsProperties(HandlebarsValueResolversProperties valueResolverProperties) {
		super(DEFAULT_PREFIX, DEFAULT_SUFFIX);
		this.valueResolversProperties = valueResolverProperties;
		this.setCache(true);
	}

	@Override
	public void applyToViewResolver(Object viewResolver) {
		super.applyToViewResolver(viewResolver);
		isInstanceOf(HandlebarsViewResolver.class, viewResolver,
			"ViewResolver is not an instance of HandlebarsViewResolver :" + viewResolver);
		HandlebarsViewResolver resolver = (HandlebarsViewResolver) viewResolver;

		List<ValueResolver> valueResolvers = new ArrayList<ValueResolver>();

		addValueResolverIfNeeded(valueResolvers, valueResolversProperties.isJavaBean(), JavaBeanValueResolver.INSTANCE);
		addValueResolverIfNeeded(valueResolvers, valueResolversProperties.isMap(), MapValueResolver.INSTANCE);
		addValueResolverIfNeeded(valueResolvers, valueResolversProperties.isField(), FieldValueResolver.INSTANCE);
		addValueResolverIfNeeded(valueResolvers, valueResolversProperties.isMethod(), MethodValueResolver.INSTANCE);

		resolver.setValueResolvers(listToArray(valueResolvers));
		resolver.setRegisterMessageHelper(registerMessageHelper);
		resolver.setFailOnMissingFile(failOnMissingFile);
	}

	public void setRegisterMessageHelper(Boolean registerMessageHelper) {
		this.registerMessageHelper = registerMessageHelper;
	}

	public void setFailOnMissingFile(Boolean failOnMissingFile) {
		this.failOnMissingFile = failOnMissingFile;
	}

	private void addValueResolverIfNeeded(List<ValueResolver> resolvers, boolean property, ValueResolver resolver) {
		if (property) {
			resolvers.add(resolver);
		}
	}

	private ValueResolver[] listToArray(List<ValueResolver> resolvers) {
		return resolvers.toArray(new ValueResolver[resolvers.size()]);
	}
}
