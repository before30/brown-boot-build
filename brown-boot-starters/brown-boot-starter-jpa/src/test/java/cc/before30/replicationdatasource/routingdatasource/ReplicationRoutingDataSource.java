package cc.before30.replicationdatasource.routingdatasource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Created by before30 on 18/12/2016.
 */
/**
 * {@link org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource}와
 * {@link org.springframework.transaction.support.TransactionSynchronizationManager}를 통해
 * Transaction의 readOnly 값에 따라 데이터 소스 분기
 */

@Slf4j
public class ReplicationRoutingDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        String dataSourceType = TransactionSynchronizationManager.isCurrentTransactionReadOnly() ? "read" : "write";
        log.info("current dataSourceType : {}", dataSourceType);
        return dataSourceType;
    }
}
