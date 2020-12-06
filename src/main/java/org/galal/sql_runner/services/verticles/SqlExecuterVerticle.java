package org.galal.sql_runner.services.verticles;

import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.core.eventbus.EventBus;
import io.vertx.mutiny.core.eventbus.Message;
import org.apache.http.HttpStatus;
import org.galal.sql_runner.services.verticles.enums.MessageStatus;
import org.galal.sql_runner.services.verticles.enums.Messages;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import static org.galal.sql_runner.services.verticles.enums.Messages.*;
import static org.galal.sql_runner.services.verticles.uitls.VertxUtils.readMessageStatus;

@ApplicationScoped
public class SqlExecuterVerticle {


    @Inject
    EventBus bus;

    @ConsumeEvent(EXECUTE_SQL)
    public Uni<JsonObject> runSql(String sqlFilePathMsg){
        return bus
                .request(GET_FILE, sqlFilePathMsg)
                .chain(this::executeSql);
    }



    private Uni<JsonObject> executeSql(Message<?> sqlFileMsg){
       String sql = (String) sqlFileMsg.body();
       JsonObject json = new JsonObject().put("sql", sql);
       return Uni.createFrom().item(json);
    }
}
