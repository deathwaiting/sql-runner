package org.galal.sql_runner.services.verticles.uitls;

import io.vertx.mutiny.core.eventbus.Message;
import org.galal.sql_runner.utils.Utils;

import static java.util.Optional.ofNullable;
import static org.apache.http.HttpStatus.SC_OK;
import static org.galal.sql_runner.services.verticles.enums.Headers.STATUS;

public class VertxUtils {

    public static Integer readMessageStatus(Message<?> message){
       return ofNullable(message.headers().get(STATUS.name()))
                .flatMap(Utils::safeIntParsing)
                .orElse(SC_OK);
    }
}
