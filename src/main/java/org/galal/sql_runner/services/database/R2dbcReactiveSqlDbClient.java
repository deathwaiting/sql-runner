package org.galal.sql_runner.services.database;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.converters.uni.UniReactorConverters;
import io.vertx.core.json.JsonArray;
import org.galal.sql_runner.services.config.DatabaseProperties;
import org.galal.sql_runner.services.verticles.FileServerVerticle;
import org.jboss.logging.Logger;
import org.springframework.data.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Mono;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

import static io.r2dbc.spi.ConnectionFactoryOptions.*;
import static io.smallrye.mutiny.converters.uni.UniReactorConverters.fromMono;
import static java.lang.String.format;
import static java.util.Optional.ofNullable;

public class R2dbcReactiveSqlDbClient implements ReactiveSqlDbClient{
    private static final Logger LOG = Logger.getLogger(ReactiveSqlDbClient.class);

    private DatabaseProperties props;
    private DatabaseClient client;

    private  ObjectMapper objectMapper;


    public R2dbcReactiveSqlDbClient(DatabaseProperties properties, ObjectMapper objectMapper){
        this.props = properties;
        this.objectMapper = objectMapper;
        createDatabaseClient();
    }



    @Override
    public Uni<String> query(String sql) {
        LOG.info(format("R2dbc running query: [%s]",sql));
        var resultMono =
                client
                    .execute(sql)
                    .fetch()
                    .all()
                    .collectList()
                    .flatMap(this::rowsToJsonArray)
                    .doOnError(e -> LOG.error(format("failed to run query[%s]",sql) ,e))
                    .doOnNext(res -> LOG.info("R2dbc ran the query!"));
        return Uni.createFrom().converter(fromMono(), resultMono);
    }



    @Override
    public Uni<Integer> execute(String sql) {
        var resultMono =  client.execute(sql).fetch().rowsUpdated();
        return  Uni.createFrom().converter(fromMono(), resultMono);
    }



    private Mono<String> rowsToJsonArray(List<Map<String,Object>> rows){
        try {
           var json = objectMapper.writeValueAsString(rows);
           return Mono.just(json);
        } catch (JsonProcessingException e) {
            LOG.error(e);
            return Mono.error(e);
        }
    }


    private void createDatabaseClient(){
        //TODO props values should be optional, and only if they exist, then add
        //them to the ConnectionOptions
        var connectionOptions =
                ConnectionFactoryOptions.builder()
                    .option(DRIVER, props.getDriver())
                    .option(PROTOCOL, props.getProtocol())
//                    .option(HOST, props.getHost())
                    .option(USER, props.getUsername())
//                    .option(PORT, props.getPort())
                    .option(PASSWORD, props.getPassword())
                    .option(DATABASE, props.getDatabase())
                    .option(SSL, ofNullable(props.isSslMode()).orElse(false))
                .build();
        var  connectionFactory = ConnectionFactories.get(connectionOptions);
        this.client = DatabaseClient.create(connectionFactory);
    }
}
