package org.galal.sql_runner.controller;

import java.util.Optional;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.vertx.core.Vertx;
import lombok.Getter;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;



@Path("/sql")
@RegisterRestClient
public class SqlResource {

    @Inject
    Vertx vertx;

    @GET
    @Path("/{file}")
    @Produces(APPLICATION_JSON)
    public Object hello(@PathParam String file) {
        return new Data(file, "my data 2");
    }
}


@Getter
class Data{
    String d1;
    String d2;

    Data(String d1, String d2){
        this.d1 = d1;
        this.d2 = d2;
    }
}

