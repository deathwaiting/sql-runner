package org.galal.sql_runner.services.verticles;

import io.quarkus.runtime.StartupEvent;
import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.vertx.core.AbstractVerticle;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.core.buffer.Buffer;
import io.vertx.mutiny.core.eventbus.Message;
import org.apache.http.HttpStatus;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static java.lang.String.format;
import static java.nio.file.Files.notExists;
import static org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR;
import static org.galal.sql_runner.services.verticles.enums.Messages.GET_FILE;

@ApplicationScoped
public class FileServerVerticle {

    private static final Logger LOG = Logger.getLogger(FileServerVerticle.class);

    @Inject
    Vertx vertx;

    @ConfigProperty(name = "org.galal.sql_runner.directory", defaultValue = "sql")
    String directoryPath;

    private final Path workingDir = FileSystems.getDefault().getPath(".").toAbsolutePath();


    public void init(@Observes StartupEvent e) {
        initializeSqlDirectory();
    }


    @ConsumeEvent(GET_FILE)
    public Uni<String> readFile(String filePathMessage){
         return Uni
                 .createFrom()
                 .item(Path.of(directoryPath))
                 .map(dir -> dir.resolve(filePathMessage))
                 .map(Path::toString)
                 .chain(vertx.fileSystem()::readFile)
                 .map(Buffer::toString);
    }


    private void initializeSqlDirectory(){
        Path path =
                Optional
                    .of(directoryPath)
                    .map(this::createSqlDirectoryPath)
                    .orElse(workingDir.resolve("sql"));
        if(notExists(path)){
            try {
                Files.createDirectory(path);
                LOG.info(format("Created SQL directory[%s]", path.toString()));
            } catch (IOException e) {
                LOG.error(format("Failed to create sql files directory[%s] ...", path.toString()), e);
            }
        }
        LOG.info(format("Using SQL directory[%s] ...", path.toString()));
    }



    private Path createSqlDirectoryPath(String pathString){
        return pathString.startsWith("/")? Path.of(pathString).toAbsolutePath() : workingDir.resolve(pathString);
    }
}

