package org.galal.sql_runner.test;

import io.agroal.api.AgroalDataSource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.galal.test_utils.TestUtils;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.nio.file.Path;

import static io.restassured.RestAssured.given;
import static org.galal.test_utils.TestUtils.executeSqlFile;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONAs;

@QuarkusTest
@QuarkusTestResource(H2DatabaseTestResource.class)
public class GreetingResourceTest {

    @Inject
    DataSource dataSource;


    @ConfigProperty(name = "quarkus.datasource.jdbc.url")
    String jdbcUrl;

    @ConfigProperty(name = "quarkus.datasource.jdbc.driver")
    String jdbcDriver;

    @Test
    public void testConfig(){
        assertEquals("jdbc:h2:tcp://localhost/mem:test", jdbcUrl);
        assertEquals("org.h2.Driver", jdbcDriver);
    }


    @Test
    public void testHelloEndpoint() {
        loadSqlForTesting("sql/test_data_insert.sql");
        String expectedJson = TestUtils.readResourceAsString("json/expected_data.json");

        given()
        .when().get("/sql/query_this.sql")
        .then()
        .statusCode(200)
        .body(sameJSONAs(expectedJson)
                .allowingExtraUnexpectedFields()
                .allowingAnyArrayOrdering());

        loadSqlForTesting("sql/clear_test_data.sql");
    }



    private void loadSqlForTesting(String sqlFilePath){
        executeSqlFile(dataSource, sqlFilePath);
    }

}
