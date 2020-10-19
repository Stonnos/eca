package eca.data.db;

import eca.config.DatabaseTestConfig;
import eca.config.EcaCoreTestConfiguration;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;

import java.util.Map;

/**
 * Base class for database integration tests.
 *
 * @author Roman Batygin
 */
public abstract class AbstractDatabaseTest {

    private final ConnectionDescriptorBuilder connectionDescriptorBuilder = new ConnectionDescriptorBuilder();

    private final EcaCoreTestConfiguration ecaCoreTestConfiguration = EcaCoreTestConfiguration.getInstance();

    @Getter
    private Map<DataBaseType, DatabaseTestConfig> databaseTestConfigMap;

    @BeforeEach
    void init() {
        databaseTestConfigMap = ecaCoreTestConfiguration.getDatabaseTestConfigMap();
    }

    ConnectionDescriptor createConnectionDescriptor(DataBaseType dataBaseType, DatabaseTestConfig databaseTestConfig) {
        ConnectionDescriptor connectionDescriptor = dataBaseType.handle(connectionDescriptorBuilder);
        connectionDescriptor.setDriver(databaseTestConfig.getDriver());
        connectionDescriptor.setDataBaseName(databaseTestConfig.getDataBaseName());
        connectionDescriptor.setHost(databaseTestConfig.getHost());
        connectionDescriptor.setLogin(databaseTestConfig.getLogin());
        connectionDescriptor.setPassword(databaseTestConfig.getPassword());
        if (databaseTestConfig.getPort() != null) {
            connectionDescriptor.setPort(databaseTestConfig.getPort());
        }
        return connectionDescriptor;
    }
}
