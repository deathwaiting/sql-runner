package org.galal.sql_runner.services.verticles;

import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.eventbus.EventBus;
import io.vertx.mutiny.core.eventbus.Message;
import org.galal.sql_runner.services.verticles.messages.SqlFilePathMsg;
import org.galal.sql_runner.services.database.ReactiveSqlDbClient;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import java.util.Map;

import static java.util.Optional.ofNullable;
import static org.galal.sql_runner.services.verticles.enums.Messages.*;

@ApplicationScoped
public class SqlExecuterVerticle {
    private static final Logger LOG = Logger.getLogger(SqlExecuterVerticle.class);

    @Inject
    EventBus bus;

    @Inject
    ReactiveSqlDbClient reactiveDbClient;

    @ConsumeEvent(EXECUTE_SQL)
    public Uni<String> executeSql(SqlFilePathMsg msg){
        return bus
                .request(GET_FILE, msg.getFile())
                .flatMap(sqlScriptMsg -> executeSql(sqlScriptMsg, msg.getParams()));
    }



    private Uni<String> executeSql(Message<?> sqlScriptMsg, Map<String,String> paramsAsMap){
       var sql = (String) sqlScriptMsg.body();
       return reactiveDbClient
               .query(sql, paramsAsMap)
               .onItem()
               .invoke(res -> LOG.info("Executor called the query!"))
               .onFailure()
               .invoke(e -> LOG.error("Executor Failed!", e));
    }




    private String getValue(Map.Entry<String,Object> entry){
        return ofNullable(entry)
                .map(Map.Entry::getValue)
                .map(Object::toString)
                .orElse(null);
    }
}
