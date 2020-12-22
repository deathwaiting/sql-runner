package org.galal.sql_runner.test.quarkus.profiles;

import com.google.common.collect.ImmutableMap;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;

import java.util.Collections;
import java.util.Map;


public class OracleDbTestProfile implements QuarkusTestProfile {

    private final String HOST = "localhost";
    private final Integer PORT = 49161;
    private final String DATABASE = "xe";
    private final String USERNAME = "system";
    private final String PASSWORD = "oracle";

    public Map<String,String> getConfigOverrides() {
        var url = String.format("jdbc:oracle:thin:@%s:%d/%s", HOST, PORT, DATABASE);

        return ImmutableMap
                .<String,String>builder()
                .put("quarkus.datasource.db-kind", "other")
                .put("quarkus.datasource.username", USERNAME)
                .put("quarkus.datasource.password", PASSWORD)
                .put("quarkus.datasource.jdbc.url", url)
                .put("quarkus.datasource.jdbc.driver", "oracle.jdbc.driver.OracleDriver")
                .put("org.galal.sql_runner.r2dbc.driver", "oracle")
                .put("org.galal.sql_runner.r2dbc.protocol", "jdbc:oracle:thin")
                .put("org.galal.sql_runner.r2dbc.database", DATABASE)
                .put("org.galal.sql_runner.r2dbc.host", HOST)
                .put("org.galal.sql_runner.r2dbc.port", PORT.toString())
                .put("org.galal.sql_runner.r2dbc.username", USERNAME)
                .put("org.galal.sql_runner.r2dbc.password", PASSWORD)
                .build();
    }
}
