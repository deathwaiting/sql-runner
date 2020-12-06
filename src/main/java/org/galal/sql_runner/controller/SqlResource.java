package org.galal.sql_runner.controller;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import io.quarkus.vertx.web.Param;
import io.quarkus.vertx.web.Route;
import io.quarkus.vertx.web.RoutingExchange;
import io.smallrye.mutiny.Uni;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.core.eventbus.EventBus;
import io.vertx.mutiny.core.eventbus.Message;
import lombok.Getter;
import org.apache.http.HttpStatus;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.galal.sql_runner.services.verticles.enums.Headers;
import org.galal.sql_runner.services.verticles.uitls.VertxUtils;
import org.galal.sql_runner.utils.Utils;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

import java.util.Optional;

import static java.util.Optional.ofNullable;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.http.HttpStatus.SC_OK;
import static org.galal.sql_runner.services.verticles.enums.Headers.STATUS;
import static org.galal.sql_runner.services.verticles.enums.Messages.EXECUTE_SQL;
import static org.galal.sql_runner.services.verticles.uitls.VertxUtils.readMessageStatus;


@ApplicationScoped
public class SqlResource {

    @Inject
    EventBus bus;

    @Route(path = "/sql/:file", produces = APPLICATION_JSON, methods = HttpMethod.GET)
    public Uni<String> runSql(@Param("file") String file) {
        return bus
                .<JsonObject>request(EXECUTE_SQL, file)
                .map(Message::body)
                .map(Object::toString);
    }

}

