package cc.before30.brown.boot.starter.jpa.datasource;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Created by before30 on 18/12/2016.
 * Copyed from https://github.com/kwon37xi/replication-datasource/blob/master/src/main/java/kr/pe/kwonnam/replicationdatasource/LazyReplicationConnectionDataSourceProxy.java
 */
@Slf4j
public class LazyReplicationConnectionDataSourceProxy implements DataSource {

    @Setter
    @Getter
    private DataSource writeDataSource;

    @Setter
    @Getter
    private DataSource readDataSource;

    private Boolean defaultAutoCommit;
    private Integer defaultTransactionIsolation;

    public LazyReplicationConnectionDataSourceProxy() {
    }

    public LazyReplicationConnectionDataSourceProxy(DataSource writeDataSource, DataSource readDataSource) {
        this.writeDataSource = writeDataSource;
        this.readDataSource = readDataSource;
        init();
    }

    public void init() {
        if (Objects.isNull(defaultAutoCommit) ||
                Objects.isNull(defaultTransactionIsolation)) {

            Connection con = null;
            try {
                con = getWriteDataSource().getConnection();
                try {
                    checkDefaultConnectionProperties(con);
                } finally {
                    con.close();
                }
            } catch (SQLException e) {
                log.warn("Could not retrieve default auto-commit and transaction isolation settsings", e);
            }
        }
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return getWriteDataSource().getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        getWriteDataSource().setLogWriter(out);
        getReadDataSource().setLogWriter(out);
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        getWriteDataSource().setLoginTimeout(seconds);
        getReadDataSource().setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return getWriteDataSource().getLoginTimeout();
    }

    ///////

    //---------------------------------------------------------------------
    // Implementation of JDBC 4.0's Wrapper interface
    //---------------------------------------------------------------------
    @Override
    @SuppressWarnings("unchecked")
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface.isInstance(this)) {
            return (T) this;
        }
        return getWriteDataSource().unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return (iface.isInstance(this) || getWriteDataSource().isWrapperFor(iface));
    }

    //---------------------------------------------------------------------
    // Implementation of JDBC 4.1's getParentLogger method
    //---------------------------------------------------------------------
    public Logger getParentLogger() {
        return java.util.logging.Logger.getLogger(java.util.logging.Logger.GLOBAL_LOGGER_NAME);
    }

    public void setDefaultAutoCommit(boolean defaultAutoCommit) {
        this.defaultAutoCommit = defaultAutoCommit;
    }

    public void setDefaultTransactionIsolation(int defaultTransactionIsolation) {
        this.defaultTransactionIsolation = defaultTransactionIsolation;
    }


    protected synchronized void checkDefaultConnectionProperties(Connection con) throws SQLException {
        if (Objects.isNull(this.defaultAutoCommit)) {
            this.defaultAutoCommit = con.getAutoCommit();
        }

        if (Objects.isNull(this.defaultTransactionIsolation)) {
            this.defaultTransactionIsolation = con.getTransactionIsolation();
        }
    }

    protected Boolean defaultAutoCommit() {
        return this.defaultAutoCommit;
    }

    protected Integer defaultTransactionIsolation() {
        return this.defaultTransactionIsolation;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return (Connection) Proxy.newProxyInstance(
                ReplicationConnectionProxy.class.getClassLoader(),
                new Class<?>[]{ReplicationConnectionProxy.class},
                new LazyReplicationConnectionInvocationHandler());
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return (Connection) Proxy.newProxyInstance(
                ReplicationConnectionProxy.class.getClassLoader(),
                new Class<?>[]{ReplicationConnectionProxy.class},
                new LazyReplicationConnectionInvocationHandler(username, password));
    }

    private static interface ReplicationConnectionProxy extends Connection {
        Connection getReplicationTargetConnection();
    }

    ///
    private class LazyReplicationConnectionInvocationHandler implements InvocationHandler {

        private String username;
        private String password;
        private Boolean readOnly = Boolean.FALSE;
        private Integer transactionIsolation;
        private Boolean autoCommit;
        private boolean closed = false;
        private Connection replicationTargetConnection;

        public LazyReplicationConnectionInvocationHandler() {
            this.autoCommit = defaultAutoCommit();
            this.transactionIsolation = defaultTransactionIsolation();
        }

        public LazyReplicationConnectionInvocationHandler(String username, String password) {
            this();
            this.username = username;
            this.password = password;
        }


        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if ("equals".equals(method.getName())) {
                return (proxy == args[0]);
            } else if ("hashCode".equals(method.getName())) {
                return System.identityHashCode(proxy);
            } else if ("unwrap".equals(method.getName())) {
                if (((Class<?>) args[0]).isInstance(proxy)) {
                    return proxy;
                }
            } else if ("isWrapperFor".equals(method.getName())) {
                if (((Class<?>) args[0]).isInstance(proxy)) {
                    return true;
                }
            } else if ("getReplicationTargetConnection".equals(method.getName())) {
                return getReplicationTargetConnection(method);
            }

            if (!hasTargetConnection()) {
                if ("toString".equals(method.getName())) {
                    return "Lazy Connection proxy for target write DataSource  [" + getWriteDataSource() + "] and target read DataSource [" + getReadDataSource() + "]";
                } else if ("isReadOnly".equals(method.getName())) {
                    return this.readOnly;
                } else if ("setReadOnly".equals(method.getName())) {
                    this.readOnly = (Boolean) args[0];
                    return null;
                } else if ("getTransactionIsolation".equals(method.getName())) {
                    if (this.transactionIsolation != null) {
                        return this.transactionIsolation;
                    }
                } else if ("setTransactionIsolation".equals(method.getName())) {
                    this.transactionIsolation = (Integer) args[0];
                    return null;
                } else if ("getAutoCommit".equals(method.getName())) {
                    if (this.autoCommit != null) {
                        return this.autoCommit;
                    }
                } else if ("setAutoCommit".equals(method.getName())) {
                    this.autoCommit = (Boolean) args[0];
                    return null;
                } else if ("commit".equals(method.getName())) {
                    return null;
                } else if ("rollback".equals(method.getName())) {
                    return null;
                } else if ("getWarnings".equals(method.getName())) {
                    return null;
                } else if ("clearWarnings".equals(method.getName())) {
                    return null;
                } else if ("close".equals(method.getName())) {
                    // Ignore: no target connection yet.
                    this.closed = true;
                    return null;
                } else if ("isClosed".equals(method.getName())) {
                    return this.closed;
                } else if (this.closed) {
                    throw new SQLException("Illegal operation: connection is closed");
                }
            }

            try {
                return method.invoke(getReplicationTargetConnection(method), args);
            } catch (InvocationTargetException ex) {
                throw ex.getTargetException();
            }
        }

        private boolean hasTargetConnection() {
            return Objects.nonNull(replicationTargetConnection);
        }

        private Connection getReplicationTargetConnection(Method operation) throws SQLException {
            if (Objects.isNull(replicationTargetConnection)) {
                log.debug("Connection to database for opertaion '{}'", operation.getName());

                log.debug("current readOnly : {}", readOnly);
                DataSource targetDataSource = (readOnly == Boolean.TRUE) ? getReadDataSource() : getWriteDataSource();

                this.replicationTargetConnection = (this.username != null) ?
                        targetDataSource.getConnection(this.username, this.password) :
                        targetDataSource.getConnection();

                checkDefaultConnectionProperties(this.replicationTargetConnection);

                if (this.readOnly) {
                    try {
                        this.replicationTargetConnection.setReadOnly(this.readOnly);
                    } catch (Exception ex) {
                        // "read-only not supported" -> ignore, it's just a hint anyway
                        log.debug("Could not set JDBC Connection read-only", ex);
                    }
                }

                if (Objects.nonNull(this.transactionIsolation) &&
                        !this.transactionIsolation.equals(defaultTransactionIsolation())) {
                    this.replicationTargetConnection.setTransactionIsolation(this.transactionIsolation);
                }

                if (Objects.nonNull(this.autoCommit) &&
                        this.autoCommit != this.replicationTargetConnection.getAutoCommit()) {
                    this.replicationTargetConnection.setAutoCommit(this.autoCommit);
                }
            } else {
                log.debug("Using existing database connection for operation '{}'", operation.getName());
            }

            return this.replicationTargetConnection;
        }
    }
}
