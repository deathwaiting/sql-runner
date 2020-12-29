package org.galal.sql_runner.services.database;

import io.smallrye.mutiny.Uni;

import java.util.Map;

public interface ReactiveSqlDbClient {
    Uni<String> query(String sql, Map<String,String> params);
    Uni<Integer> execute(String sql, Map<String,String> params);
}
