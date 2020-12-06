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

dependencies {
    implementation(enforcedPlatform("${quarkusPlatformGroupId}:${quarkusPlatformArtifactId}:${quarkusPlatformVersion}"))
    implementation("io.quarkus:quarkus-rest-client-jackson")
    implementation("io.quarkus:quarkus-vertx")
    implementation("io.quarkus:quarkus-vertx-web")
//    implementation("io.quarkus:quarkus-smallrye-openapi")
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

    testCompileOnly("org.projectlombok:lombok:1.18.16")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.16")
    testImplementation("io.quarkus:quarkus-junit5")
    testImplementation("io.rest-assured:rest-assured")
    testImplementation("io.quarkus:quarkus-test-h2")
    testImplementation("org.jdbi:jdbi3-core:3.1.0")
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
