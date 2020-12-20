package org.galal.sql_runner.services.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.galal.sql_runner.services.database.OracleReactiveSqlDbClient;
import org.galal.sql_runner.services.database.R2dbcReactiveSqlDbClient;
import org.galal.sql_runner.services.database.ReactiveSqlDbClient;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;

@ApplicationScoped
public class ReactiveSqlDbClientConfig {

    private final static Set<String> R2DBC_SUPPORTED_DATABASES = Set.of("h2", "mysql", "mssql", "mariadb", "postgres");

    @Inject
    DatabaseProperties props;

    @Inject
    ObjectMapper objectMapper;

    @Produces
    @ApplicationScoped
    public ReactiveSqlDbClient createClient(){
        String db = ofNullable(props.getDriver())
                .orElseThrow(() -> new IllegalStateException("property org.galal.sql_runner.r2dbc.driver is not provided!"));
        if(R2DBC_SUPPORTED_DATABASES.contains(db)){
            return new R2dbcReactiveSqlDbClient(props, objectMapper);
        }else if(Objects.equals(db, "oracle")){
            return new OracleReactiveSqlDbClient(props, objectMapper);
        }
        throw new IllegalStateException(format("Driver type[%s] , provided by org.galal.sql_runner.r2dbc.driver is not supported!", db));
    }
}
