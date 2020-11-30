package io.quarkus.vertx.verticles;

import io.quarkus.runtime.StartupEvent;
import io.smallrye.mutiny.vertx.core.AbstractVerticle;
import io.vertx.mutiny.core.Vertx;
import org.galal.sql_runner.services.verticles.FileServerVerticle;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.inject.Singleton;

import static java.lang.String.format;

@Singleton
public class VerticleDeployer {
    private static final Logger LOG = Logger.getLogger(FileServerVerticle.class);

    public void init(@Observes StartupEvent e, Vertx vertx, Instance<AbstractVerticle> verticles) {
        for (AbstractVerticle verticle : verticles) {
            LOG.info(format("Deploying verticle[%s]...", verticle.getClass().getName()));
            vertx.deployVerticle(verticle).await().indefinitely();
        }
    }
}