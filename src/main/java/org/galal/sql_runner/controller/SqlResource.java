package org.galal.sql_runner.controller;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.quarkus.vertx.web.Param;
import io.quarkus.vertx.web.Route;
import io.quarkus.vertx.web.RouteBase;
import io.smallrye.mutiny.Uni;
import io.vertx.core.http.HttpMethod;
import io.vertx.mutiny.core.eventbus.EventBus;
import io.vertx.mutiny.core.eventbus.Message;
import io.vertx.reactivex.ext.web.RoutingContext;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.galal.sql_runner.services.verticles.messages.SqlFilePathMsg;
import org.jboss.logging.Logger;


import java.util.Map;

import static java.lang.String.format;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.StreamSupport.stream;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType.HTTP;
import static org.galal.sql_runner.services.verticles.enums.Messages.EXECUTE_SQL;


@ApplicationScoped
@SecurityScheme(securitySchemeName = "Basic Auth", type = HTTP, scheme = "basic")
@RouteBase(path = "sql", produces = APPLICATION_JSON)
public class SqlResource {
    private static final Logger LOG = Logger.getLogger(SqlResource.class);

    @Inject
    EventBus bus;

    @Route(path = "/:file", methods = HttpMethod.GET)
    public Uni<String> runSql(@Param("file") String file, RoutingContext context) {
        var queryParams =
                stream(context.queryParams().spliterator(), false)
                 .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
        var msg = new SqlFilePathMsg(file, queryParams);
        return bus
                .request(EXECUTE_SQL, msg)
                .map(Message::body)
                .map(Object::toString)
                .onItem()
                .invoke(res -> LOG.info("Returning Query result!"))
                .onFailure()
                .invoke(e -> LOG.error(format("Failed to run sql file [%s]!", file), e));
    }

}


