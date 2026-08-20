plugins {
    java
    id("com.google.protobuf") version "0.9.5"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

group = "io.github.ivanchintov"
version = "0.1.0-SNAPSHOT"

val grpcVersion = "1.76.0"
val protobufVersion = "4.32.0"

repositories {
    mavenCentral()
}

dependencies {
    // This is the server/client transport
    implementation("io.grpc:grpc-netty-shaded:$grpcVersion")
    // Serialization and deserialization
    implementation("io.grpc:grpc-protobuf:$grpcVersion")
    // Client API
    implementation("io.grpc:grpc-stub:$grpcVersion")
    implementation("com.google.protobuf:protobuf-java:${protobufVersion}")
    implementation("commons-validator:commons-validator:1.11.0")
    // Java annotations compatibility
    compileOnly("org.apache.tomcat:annotations-api:6.0.53")

    testImplementation("org.mockito:mockito-core:5.23.0")
    testImplementation("org.assertj:assertj-core:3.27.7")
    testImplementation(platform("org.junit:junit-bom:6.0.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:$protobufVersion"
    }

    plugins {
        create("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:$grpcVersion"
        }
    }

    generateProtoTasks {
        all().forEach {
            it.plugins {
                create("grpc")
            }
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}