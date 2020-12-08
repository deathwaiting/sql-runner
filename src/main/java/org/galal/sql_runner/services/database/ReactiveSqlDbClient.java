package org.galal.sql_runner.services.database;

import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonArray;

public interface ReactiveSqlDbClient {
    Uni<JsonArray> query(String sql);
    Uni<Integer> execute(String sql);
}
