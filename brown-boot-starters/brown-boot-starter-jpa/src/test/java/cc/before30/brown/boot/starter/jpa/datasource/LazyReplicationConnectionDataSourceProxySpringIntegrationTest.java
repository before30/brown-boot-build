package cc.before30.brown.boot.starter.jpa.datasource;

import cc.before30.brown.boot.starter.jpa.datasource.config.WithLazyReplicationConnectionDataSourceProxyConfig;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

/**
 * Created by before30 on 18/12/2016.
 */

@ContextConfiguration(classes = {WithLazyReplicationConnectionDataSourceProxyConfig.class})
@DirtiesContext
public class LazyReplicationConnectionDataSourceProxySpringIntegrationTest extends AbstractReplicationDataSourceIntegrationTest {
}
