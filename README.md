# chainctl-gradle-plugin

A Gradle plugin that verifies resolved JAR dependencies against [Chainguard Libraries](https://www.chainguard.dev/libraries) using `chainctl` before fat JAR assembly.

## What it does

After dependency resolution and before fat JAR tasks (`shadowJar`, `bootJar`), the plugin:

1. Walks all resolvable Gradle configurations
2. Collects the actual resolved JAR file paths from the Gradle cache
3. Runs `chainctl libraries verify <path-to-jar>` for each JAR
4. Fails the build (configurable) if any JAR is not verified

## Requirements

- [chainctl](https://edu.chainguard.dev/chainguard/chainguard-enforce/how-to-install-chainctl/) installed and on `PATH` (or configured via `chainctlPath`)
- Gradle 7.x or 8.x
- JVM 11+

## Usage

Apply the plugin in your `build.gradle.kts`:

```kotlin
plugins {
    id("com.chainguard.chainctl") version "0.1.0"
}
```

Or in Groovy `build.gradle`:

```groovy
plugins {
    id 'com.chainguard.chainctl' version '0.1.0'
}
```

### Running manually

```bash
./gradlew chainctlVerifyDependencies
```

### Automatic integration

When `shadowJar` (Shadow plugin) or `bootJar` (Spring Boot plugin) tasks are present, verification runs automatically before fat JAR assembly.

## Configuration

```kotlin
chainctlVerify {
    // Fail the build if any JAR fails verification (default: true)
    failOnUnverified = true

    // Configuration names to skip (default: empty — all resolvable configs are checked)
    skipConfigurations = listOf("testRuntimeClasspath", "testCompileClasspath")

    // Path to chainctl binary (default: "chainctl" resolved from PATH)
    chainctlPath = "/usr/local/bin/chainctl"
}
```

## Fat JAR caveat

When building a fat/uber JAR (e.g. with the Shadow plugin or Spring Boot), all dependency classes are merged into a single JAR. Verification must run **before** this assembly step — once dependencies are bundled together, individual provenance is lost. This plugin hooks `chainctlVerifyDependencies` as a dependency of `shadowJar`/`bootJar` so that verification happens on the original, individual JARs from the Gradle dependency cache.

If you are using a different fat JAR plugin, add the dependency manually:

```kotlin
tasks.named("myFatJarTask") {
    dependsOn("chainctlVerifyDependencies")
}
```

## Development

```bash
./gradlew test        # run unit tests
./gradlew build       # build and test
```
