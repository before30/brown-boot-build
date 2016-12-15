package cc.before30.boot.memcached.autoconfigure;

import net.spy.memcached.MemcachedClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by before30 on 15/12/2016.
 */

@Configuration
public class MemcachedAutoConfiguration {
    private static final String LOCALHOST = "localhost";
    private static final int DEFAULT_PORT = 11211;
    private static final String MEMCACHED_SERVERS = "memcached.servers";

    @Autowired private Environment env;

    @Bean
    public MemcachedClient memcachedClient() throws IOException {
        final List<InetSocketAddress> addresses = new ArrayList<InetSocketAddress>();
        Optional.ofNullable("").orElse("hello");

        final String servers = Optional
                .ofNullable(env.getProperty(MEMCACHED_SERVERS)).orElse(LOCALHOST + ":" + DEFAULT_PORT);

        for (final String server : servers.split(",")) {
            final int colon = server.indexOf(":");
            if (colon == -1) {
                addresses.add(getInetSocketAddress(server, DEFAULT_PORT));
            } else {
                final int port = Integer.parseInt(server.substring(colon + 1));
                final String host = server.substring(0, colon);
                addresses.add(getInetSocketAddress(host, port));
            }
        }

        return new MemcachedClient(addresses);
    }

    private InetSocketAddress getInetSocketAddress(String host, int port) {
        return new InetSocketAddress(host, port);
    }

}
