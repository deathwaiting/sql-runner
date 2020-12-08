package org.galal.sql_runner.controller;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.quarkus.vertx.web.Param;
import io.quarkus.vertx.web.Route;
import io.quarkus.vertx.web.RouteBase;
import io.smallrye.mutiny.Uni;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.core.eventbus.EventBus;
import io.vertx.mutiny.core.eventbus.Message;
import io.vertx.reactivex.ext.web.RoutingContext;
import org.galal.sql_runner.services.verticles.SqlExecuterVerticle;
import org.jboss.logging.Logger;


import static java.lang.String.format;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.galal.sql_runner.services.verticles.enums.Messages.EXECUTE_SQL;


@ApplicationScoped
@RouteBase(path = "sql", produces = APPLICATION_JSON)
public class SqlResource {
    private static final Logger LOG = Logger.getLogger(SqlResource.class);

    @Inject
    EventBus bus;

    @Route(path = "/:file", methods = HttpMethod.GET)
    public Uni<String> runSql(@Param("file") String file) {
        return bus
                .request(EXECUTE_SQL, file)
                .map(Message::body)
                .map(Object::toString)
                .onItem()
                .invoke(res -> LOG.info("Returning Query result!"))
                .onFailure()
                .invoke(e -> LOG.error(format("Failed to run sql file [%s]!", file), e));
    }

}

