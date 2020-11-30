package org.galal.sql_runner.controller;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import io.smallrye.mutiny.Uni;
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


@Path("/sql")
@RegisterRestClient
public class SqlResource {

    @Inject
    EventBus bus;

    @GET
    @Path("/{file}")
    @Produces(APPLICATION_JSON)
    public Uni<Response> hello(@PathParam String file) {
        return bus
                .<JsonObject>request(EXECUTE_SQL.name(), file)
                .onItem()
                .transform(this::createResponse);
    }



    private Response createResponse(Message<?> message){
        int status = readMessageStatus(message);
       return Response.status(status).entity(message.body()).build();
    }
}

