package org.galal.sql_runner.test;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectSpy;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactoryOptions;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.galal.sql_runner.services.config.DatabaseProperties;
import org.galal.sql_runner.services.verticles.uitls.FileReader;
import org.json.JSONArray;
import org.json.JSONException;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.data.r2dbc.core.DatabaseClient;

import javax.inject.Inject;
import javax.sql.DataSource;

import static io.r2dbc.spi.ConnectionFactoryOptions.*;
import static io.r2dbc.spi.ConnectionFactoryOptions.SSL;
import static io.restassured.RestAssured.given;
import static java.util.Optional.ofNullable;
import static org.galal.test_utils.TestUtils.executeSqlFile;
import static org.galal.test_utils.TestUtils.readResourceAsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONArrayAs;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONAs;

@QuarkusTest
@QuarkusTestResource(H2DatabaseTestResource.class)
public class SqlResourceTest {

    @Inject
    DataSource dataSource;

    @Inject
    DatabaseProperties props;

    @ConfigProperty(name = "org.galal.sql_runner.directory", defaultValue = "sql")
    String directoryPath;



    @BeforeEach
    public void loadDBTables(){
        loadSqlForTesting("sql/test_data_insert.sql");
    }



    @AfterEach
    public void clearDb(){
        loadSqlForTesting("sql/clear_test_data.sql");
    }





    @Test
    public void runSqlFileTest() throws JSONException {
        String expectedJsonStr = readResourceAsString("json/expected_data.json");
        JSONArray expectedArray = new JSONArray(expectedJsonStr);

        given()
        .when().get("/sql/query_this.sql")
        .then()
        .statusCode(200)
        .body(sameJSONAs(expectedJsonStr)
                .allowingExtraUnexpectedFields()
                .allowingAnyArrayOrdering());
    }




    @Test
    public void testDataClient(){
        var connectionOptions =
                ConnectionFactoryOptions.builder()
                        .option(DRIVER, props.getDriver())
                        .option(PROTOCOL, props.getProtocol())
//                    .option(HOST, props.getHost())
                        .option(USER, props.getUsername())
//                    .option(PORT, props.getPort())
                        .option(PASSWORD, props.getPassword())
                        .option(DATABASE, props.getDatabase())
                        .option(SSL, ofNullable(props.isSslMode()).orElse(false))
                        .build();
        var  connectionFactory = ConnectionFactories.get(connectionOptions);
        var client = DatabaseClient.create(connectionFactory);
        var res = client.execute("select * from car").fetch().all().collectList().block();
        assertFalse(res.isEmpty());
    }




    @Test
    public void fileContentCachingTest() throws JSONException {
        String expectedJsonStr = readResourceAsString("json/expected_data.json");
        JSONArray expectedArray = new JSONArray(expectedJsonStr);

        given()
                .when().get("/sql/query_this.sql")
                .then()
                .statusCode(200)
                .body(sameJSONAs(expectedJsonStr)
                        .allowingExtraUnexpectedFields()
                        .allowingAnyArrayOrdering());

        //read the file again
        given()
                .when().get("/sql/query_this.sql")
                .then()
                .statusCode(200)
                .body(sameJSONAs(expectedJsonStr)
                        .allowingExtraUnexpectedFields()
                        .allowingAnyArrayOrdering());
    }



    private void loadSqlForTesting(String sqlFilePath){
        executeSqlFile(dataSource, sqlFilePath);
    }

}
