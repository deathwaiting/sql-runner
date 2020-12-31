import org.apache.maven.properties.internal.SystemProperties

plugins {
    java
    id("io.quarkus")
    id("org.beryx.runtime") version "1.12.1"
}

repositories {
    mavenLocal()
    mavenCentral()
}

val quarkusPlatformGroupId: String by project
val quarkusPlatformArtifactId: String by project
val quarkusPlatformVersion: String by project
configurations.all{
    //solving conflicts of h2 versions between r2dbc h2-client and quarkus
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
    implementation("io.quarkus:quarkus-cache")
    implementation("io.quarkus:quarkus-smallrye-metrics")
    implementation("io.quarkus:quarkus-reactive-db2-client")
    implementation("io.quarkus:quarkus-agroal")
    implementation("io.quarkus:quarkus-elytron-security-properties-file")
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
val jarName = "sql-runner-${version}"
val jreDirSuffix = "jre"
val jreDir = "$buildDir/jre/${project.name}-$jreDirSuffix"



runtime {
    options.set(listOf("--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages"))
    //needed to configure jlink task
    modules.set(
            listOf(
                "java.sql",
                "java.naming",
                "java.desktop",
                "jdk.management",
                "java.management",
                "jdk.unsupported",
                "java.rmi"))

    //select the OS specific block for providing the jre that will be bundled with the application
    //jre is built using jlink tool.
    //it is possible to provide the path of the jdk or even download it
    //This uses "The Badass Runtime plugin" to create the jre, for more info. please check the
    //plugin documentation https://badass-runtime-plugin.beryx.org/releases/latest/
    targetPlatform(jreDirSuffix) {
        setJdkHome(System.getProperty("java.home"))
        addOptions("--endian", "big")
    }


//    targetPlatform(jreDirSuffix) {
//        setJdkHome(jdkDownload("https://github.com/AdoptOpenJDK/openjdk14-binaries/releases/download/jdk-14.0.1%2B7.1/OpenJDK14U-jdk_x64_windows_hotspot_14.0.1_7.zip"))
//        setBuildDir(jreDir)
//        addOptions("--output", "$jreDir")
//    }
}




java {
    sourceCompatibility = JavaVersion.VERSION_14
    targetCompatibility = JavaVersion.VERSION_14
}



quarkus(){
    setFinalName(jarName)
}



tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-parameters")
}


tasks["runtime"].doLast {
    copy {
        from("src/main/resources/application.yml")
        into("$buildDir/image/bin/config")
    }
}


tasks.register("testing"){
    dependsOn("jre")
    println(tasks["jre"].outputs.files.asPath)
}


tasks.register("jarWithJRE"){
    System.setProperty("quarkus.package.type", "uber-jar")
    val dependencies = listOf("build", "jre")
    mustRunAfter(dependencies)
    dependsOn(dependencies)

    doLast{
        copy {
            from("src/main/resources/application.yml")
            into("$buildDir/dist_with_jre/config")
        }
        copy {
            from("src/main/resources/run.sh", "src/main/resources/run.bat", "$buildDir/${jarName}-runner.jar")
            into("$buildDir/dist_with_jre")
        }
        copy {
            from( jreDir)
            into("$buildDir/dist_with_jre/jre")
        }
    }
}
