package org.galal.test_utils;

import org.jboss.logging.Logger;
import org.jdbi.v3.core.Jdbi;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.lang.String.format;

public class TestUtils {

    private static final Logger LOG = Logger.getLogger(TestUtils.class);

    public static void executeSqlFile(DataSource dataSource, String sqlFilePath){
        LOG.info(format("running sql file [%s] ...", sqlFilePath));

        Path workingDir = FileSystems.getDefault().getPath(".").toAbsolutePath();
        Path path = sqlFilePath.startsWith("/") ? Path.of(sqlFilePath) : workingDir.resolve("src/test/resources").resolve(sqlFilePath);
        try {
            var sql = Files.readString(path);
            Jdbi
             .create(dataSource)
             .withHandle( h -> h.execute(sql));
        } catch (IOException e) {
            e.printStackTrace();
            LOG.error(e);
            throw new RuntimeException("Failed to run sql file!", e);
        }
    }



    public static String readResourceAsString(String resourceRelativePath){
        Path workingDir = FileSystems.getDefault().getPath(".").toAbsolutePath();
        Path path = workingDir.resolve("src/test/resources").resolve(resourceRelativePath);
        try {
           return Files.readString(path);
        } catch (IOException e) {
            LOG.error(e);
            throw new RuntimeException(format("Failed to read resource[%s]!", path.toAbsolutePath().toString()), e);
        }
    }
}
