plugins {
    java
    id("io.quarkus")
}

repositories {
    mavenLocal()
    mavenCentral()
}

val quarkusPlatformGroupId: String by project
val quarkusPlatformArtifactId: String by project
val quarkusPlatformVersion: String by project
configurations.all{
    resolutionStrategy{
        force("com.h2database:h2:1.4.200")
    }
}
dependencies {
    implementation(enforcedPlatform("${quarkusPlatformGroupId}:${quarkusPlatformArtifactId}:${quarkusPlatformVersion}"))
    implementation("io.quarkus:quarkus-rest-client-jackson")
    implementation("io.quarkus:quarkus-vertx")
    implementation("io.quarkus:quarkus-vertx-web")
    implementation("io.quarkus:quarkus-smallrye-openapi")
    implementation("io.quarkus:quarkus-config-yaml")
    implementation("io.quarkus:quarkus-jackson")
    implementation("io.quarkus:quarkus-logging-json")
    implementation("io.quarkus:quarkus-cache")
    implementation("io.quarkus:quarkus-smallrye-metrics")
    implementation("io.quarkus:quarkus-reactive-db2-client")
    implementation("io.quarkus:quarkus-agroal")
//    implementation("io.quarkus:quarkus-micrometer")
    implementation("io.quarkus:quarkus-arc")
    implementation("io.quarkus:quarkus-resteasy")
    implementation("io.quarkus:quarkus-resteasy-mutiny")
    compileOnly("org.projectlombok:lombok:1.18.16")
    annotationProcessor("org.projectlombok:lombok:1.18.16")
    implementation("uk.co.datumedge:hamcrest-json:0.2")
    implementation("io.netty:netty-transport-native-epoll:linux-x86_64")
    implementation("io.netty:netty-transport-native-kqueue:osx-x86_64")
    //need at most 1.1.4.RELEASE, because quarkus depends on spring-core 5.2.9.RELEASE, and later version
    //of r2dbc client uses higher versions of spring core causing dependency conflicts
    implementation("org.springframework.data:spring-data-r2dbc:1.1.4.RELEASE")
    implementation("io.r2dbc:r2dbc-h2:0.8.2.RELEASE")
    implementation("org.mariadb:r2dbc-mariadb:0.8.4-rc")
    implementation("io.r2dbc:r2dbc-mssql:0.8.2.RELEASE")
    implementation("dev.miku:r2dbc-mysql:0.8.2.RELEASE")
    implementation("io.r2dbc:r2dbc-postgresql:0.8.6.RELEASE")
    implementation("io.smallrye.reactive:mutiny-reactor:0.11.0")
    implementation("com.oracle.database.jdbc:ojdbc10-production:19.8.0.0")
    implementation("org.jdbi:jdbi3-core:3.1.0")

    testCompileOnly("org.projectlombok:lombok:1.18.16")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.16")
    testImplementation("io.quarkus:quarkus-junit5")
    testImplementation("io.quarkus:quarkus-junit5-mockito")
    testImplementation("io.rest-assured:rest-assured")
    testImplementation("io.quarkus:quarkus-test-h2")

}

group = "org.galal"
version = "1.0.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-parameters")
}
