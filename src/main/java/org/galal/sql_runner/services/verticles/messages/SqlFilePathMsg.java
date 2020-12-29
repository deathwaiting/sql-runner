package org.galal.sql_runner.services.verticles.messages;

import lombok.Value;

import java.util.Map;

@Value
public class SqlFilePathMsg {
    String file;
    Map<String, String> params;
}
