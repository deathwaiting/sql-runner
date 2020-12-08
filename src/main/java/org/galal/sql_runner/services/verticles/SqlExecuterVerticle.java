package org.galal.sql_runner.services.verticles;

import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.core.eventbus.EventBus;
import io.vertx.mutiny.core.eventbus.Message;
import org.galal.sql_runner.services.database.ReactiveSqlDbClient;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import static org.galal.sql_runner.services.verticles.enums.Messages.*;
import static org.galal.sql_runner.services.verticles.uitls.VertxUtils.readMessageStatus;

@ApplicationScoped
public class SqlExecuterVerticle {
    private static final Logger LOG = Logger.getLogger(SqlExecuterVerticle.class);

    @Inject
    EventBus bus;

    @Inject
    ReactiveSqlDbClient reactiveDbClient;

    @ConsumeEvent(EXECUTE_SQL)
    public Uni<String> executeSql(String sqlFilePathMsg){
        return bus
                .request(GET_FILE, sqlFilePathMsg)
                .flatMap(this::executeSql);
    }



    private Uni<String> executeSql(Message<?> sqlFileMsg){
       String sql = (String) sqlFileMsg.body();
       return reactiveDbClient
               .query(sql)
               .onItem()
               .invoke(res -> LOG.info("Executor called the query!"))
               .onFailure()
               .invoke(e -> LOG.error("Executor Failed!", e));
    }
}
