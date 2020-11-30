package org.galal.sql_runner.services.verticles;

import io.quarkus.runtime.StartupEvent;
import io.smallrye.mutiny.vertx.core.AbstractVerticle;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static java.lang.String.format;
import static java.nio.file.Files.notExists;

@ApplicationScoped
public class FileServerVerticle extends AbstractVerticle {

    private static final Logger LOG = Logger.getLogger(FileServerVerticle.class);

    @ConfigProperty(name = "org.galal.sql_runner.directory", defaultValue = "sql")
    String directoryPath;

    private final Path workingDir = FileSystems.getDefault().getPath(".").toAbsolutePath();


    public void init(@Observes StartupEvent e) {
        initializeSqlDirectory();
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

