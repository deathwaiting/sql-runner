package org.galal.sql_runner.services.database;

import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonArray;

public interface ReactiveSqlDbClient {
    Uni<String> query(String sql);
    Uni<Integer> execute(String sql);
}
