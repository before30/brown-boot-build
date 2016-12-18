package cc.before30.brown.boot.starter.jpa.datasource.config;


import cc.before30.brown.boot.starter.jpa.datasource.LazyReplicationConnectionDataSourceProxy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import javax.sql.DataSource;
/**
 * Created by before30 on 18/12/2016.
 */

@Configuration
public class WithLazyReplicationConnectionDataSourceProxyConfig {
    @Bean(destroyMethod = "shutdown")
    public DataSource writeDataSource() {
        EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder()
                .setName("lazyWriteDb")
                .setType(EmbeddedDatabaseType.H2)
                .setScriptEncoding("UTF-8")
                .addScript("classpath:/writedb.sql");
        return builder.build();
    }

    @Bean(destroyMethod = "shutdown")
    public DataSource readDataSource() {
        EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder()
                .setName("lazyReadDb")
                .setType(EmbeddedDatabaseType.H2)
                .setScriptEncoding("UTF-8")
                .addScript("classpath:/readdb.sql");
        return builder.build();
    }

    @Bean
    public DataSource dataSource(@Qualifier("writeDataSource") DataSource writeDataSource, @Qualifier("readDataSource") DataSource readDataSource) {
        return new LazyReplicationConnectionDataSourceProxy(writeDataSource, readDataSource);
    }
}
