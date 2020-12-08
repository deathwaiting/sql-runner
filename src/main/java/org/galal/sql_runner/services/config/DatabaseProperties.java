package org.galal.sql_runner.services.config;

import io.quarkus.arc.config.ConfigProperties;
import lombok.Data;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ConfigProperties(prefix = "org.galal.sql_runner.r2dbc",failOnMismatchingMember = false)
@Data
public class DatabaseProperties {
    private String driver;
    private String protocol;
    private String password;
    private String username;
    private String host = null;
    private Integer port = null;
    private String database;
    private boolean sslMode = false;
}

