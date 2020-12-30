package org.galal.sql_runner.test;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import org.galal.sql_runner.test.quarkus.profiles.OracleDbTestProfile;
import org.json.JSONArray;
import org.json.JSONException;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.sql.DataSource;

import static io.restassured.RestAssured.given;
import static org.galal.test_utils.TestUtils.readResourceAsString;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONAs;


/**
 * This test should run only if there is an available oracle database with the connection info
 * used in OracleDbTestProfile.
 * consider using this docker image for testing
 * docker pull oracleinanutshell/oracle-xe-11g
 * docker run -p 49161:1521 oracleinanutshell/oracle-xe-11g
 * */
@QuarkusTest
@TestProfile(OracleDbTestProfile.class)
public class SqlResourceAgainstOracleDbTest {

    @Inject
    DataSource dataSource;

    private String username = "test-admin";
    private String password = "d0ntUseTh1s";

    @Test
    public void runSqlFileOnOracleDbTest() throws JSONException {
        try{
            dataSource.getConnection();
        }catch(Exception e){
            //if the database is not connected ignore the test
            return;
        }
        String expectedJsonStr = readResourceAsString("json/expected_data.json");
        JSONArray expectedArray = new JSONArray(expectedJsonStr);

        given()
            .when()
                .auth()
                .basic(username, password)
            .get("/sql/query_oracle.sql")
            .prettyPeek()
            .then()
            .statusCode(200)
            .body(sameJSONAs(expectedJsonStr)
                    .allowingExtraUnexpectedFields()
                    .allowingAnyArrayOrdering());
    }
}
