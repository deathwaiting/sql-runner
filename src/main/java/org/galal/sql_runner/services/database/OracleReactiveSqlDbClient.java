package org.galal.sql_runner.services.database;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.agroal.api.AgroalDataSource;
import io.agroal.api.configuration.supplier.AgroalDataSourceConfigurationSupplier;
import io.agroal.api.security.NamePrincipal;
import io.agroal.api.security.SimplePassword;
import io.smallrye.mutiny.Uni;
import oracle.jdbc.driver.OracleDriver;
import org.galal.sql_runner.services.config.DatabaseProperties;
import org.jboss.logging.Logger;
import org.jdbi.v3.core.Jdbi;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;

public class OracleReactiveSqlDbClient implements ReactiveSqlDbClient{
    private static final Logger LOG = Logger.getLogger(OracleReactiveSqlDbClient.class);
    private static final int MIN_POOL_SIZE = 5;
    private static final int MAX_POOL_SIZE = 100;

    private DatabaseProperties props;
    private  ObjectMapper objectMapper;
    private AgroalDataSource dataSource;
    private Jdbi jdbi;




    public OracleReactiveSqlDbClient(DatabaseProperties props, ObjectMapper objectMapper) {
        this.props = props;
        this.objectMapper = objectMapper;
        createDatabaseConnectionPool();
    }



    @Override
    public Uni<String> query(String sql) {
        return Uni.createFrom().item(() -> this.doRunQuery(sql));
    }



    private String doRunQuery(String sql){
        try {
            List<Map<String,Object>> result =
                    this.jdbi
                            .withHandle(
                                    h -> h.createQuery(sql)
                                            .mapToMap()
                                            .list());
            return objectMapper.writeValueAsString(result);
        } catch (JsonProcessingException e) {
            LOG.error(e,e);
            throw new RuntimeException(e);
        }
    }



    @Override
    public Uni<Integer> execute(String sql) {
        return Uni.createFrom().item(() -> this.jdbi.withHandle(h -> h.execute(sql)));
    }




    private void createDatabaseConnectionPool() {
        LOG.info("Creating agroal connection pool for oracle database...");
        String url = format("jdbc:oracle:thin:@%s:%d/%s", props.getHost(), props.getPort(), props.getDatabase());
        AgroalDataSourceConfigurationSupplier configuration =
                new AgroalDataSourceConfigurationSupplier()
                        .connectionPoolConfiguration(
                                cp -> cp.minSize( MIN_POOL_SIZE )
                                        .maxSize( MAX_POOL_SIZE )
                                        .connectionFactoryConfiguration(
                                                cf -> cf.connectionProviderClass( OracleDriver.class )
                                                        .jdbcUrl(url)
                                                        .principal(new NamePrincipal( props.getUsername()))
                                                        .credential(new SimplePassword( props.getPassword())
                                                        ) ));
        try {
            this.dataSource = AgroalDataSource.from( configuration );
            this.jdbi = Jdbi.create(this.dataSource);
        } catch ( SQLException e ) {
            LOG.error( "Failed to create Oracle Connection pool", e );
        }
    }
}
