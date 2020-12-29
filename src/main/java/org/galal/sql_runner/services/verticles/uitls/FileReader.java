package org.galal.sql_runner.services.verticles.uitls;

import io.quarkus.cache.CacheInvalidate;
import io.quarkus.cache.CacheResult;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.core.buffer.Buffer;
import org.galal.sql_runner.services.verticles.FileServerVerticle;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;


@ApplicationScoped
public class FileReader {

    private static final Logger LOG = Logger.getLogger(FileReader.class);

    @Inject
    Vertx vertx;

    @CacheResult(cacheName = "FILE_CONTENT")
    public Uni<String> readFileFromPath(String path){
        LOG.info(String.format("reading contents of file[%s]", path));
        return vertx
                .fileSystem()
                .readFile(path)
                .map(Buffer::toString);
    }
}
