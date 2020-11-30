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
public class SqlExecuterVerticle extends AbstractVerticle {


    @Inject
    EventBus bus;

    @ConsumeEvent(value = EXECUTE_SQL)
    public Message<JsonObject> runSql(Message<String> sqlFilePath){
        return bus
                .request(GET_FILE, sqlFilePath)
                .onItem()
                .transform()
                .chain(this::executeSql)
    }



    private Message<JsonObject> executeSql(Message<String> sqlFileMsg){
        int status = readMessageStatus(sqlFileMsg);
        if(status != HttpStatus.SC_OK){
            return Message.
        }
        return bus
                .request(RUN_SQL, sql)
                .
    }
}
