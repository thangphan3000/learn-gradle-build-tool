# Learn Gradle Build Tool (Java Multi-Module)

This repository is a Java multi-module Gradle project with:

- `app`: runnable application module
- `util`: reusable library module

## Folder structure

```text
.
├── app/                                # Application module
│   ├── build.gradle
│   └── src/
│       ├── main/java/com/bank/App.java
│       └── test/java/com/bank/AppTest.java
├── util/                               # Shared library module
│   ├── build.gradle
│   └── src/
│       ├── main/java/com/bank/util/Utils.java
│       └── test/java/com/bank/util/UtilsTest.java
├── gradle/
│   ├── libs.versions.toml              # Dependency version catalog
│   └── wrapper/
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── settings.gradle                     # Declares included modules
├── gradle.properties                   # Gradle runtime/config flags
├── gradlew                             # Unix wrapper
├── gradlew.bat                         # Windows wrapper
└── README.md
```

## How Java structure works

Each module follows the standard source layout:

- `src/main/java`: production code
- `src/test/java`: test code

Package names map directly to folders.

Example:

```java
package com.bank.util;
```

must be in:

```text
src/main/java/com/bank/util/Utils.java
```

This mapping is how Java and Gradle find and compile classes correctly.

## Module responsibilities

### app

- Uses `application` plugin
- Has the entrypoint (`main` method)
- Depends on `util` via `implementation project(':util')`

### util

- Uses `java-library` plugin
- Contains reusable logic (`Utils.sum`)
- Can be consumed by `app` or future modules

## Key Gradle files

- `settings.gradle`: lists modules in this build (`include('app')`, `include('util')`)
- `app/build.gradle`, `util/build.gradle`: module-specific plugins, dependencies, test config
- `gradle/libs.versions.toml`: central version/dependency catalog
- `gradle.properties`: global Gradle behavior (for example, configuration cache)

## Plugin Types
- There are three plugin types: core, community, and local.

### Core
- Shipped with Gradle distribution
- https://docs.gradle.org/current/userguide/plugin_reference.html

### Community
- Download from plugin repository
- Need to specify version
- [[https://plugins.gradle.org]](Gradle plugins)

```gradle
plugins {
    id 'application' // Core
    id 'org.other.plugin' version '1.4.0' // Community
}
```

### Local
- Implement locally

### Example: Java plugins
- `java` plugin
  - Configures source sets (`SourceSet`) such as `src/main/java` and `src/test/java`
  - Adds common tasks like `compileJava`, `test`, and `jar`

> Note: src/main/java is the default convention from the Java plugin (SourceSet defaults).

- `java-library` plugin
  - Applies `java` automatically
  - Adds library-specific dependency separation (`api` vs `implementation`)

- `application` plugin
  - Build configuration for `main` class
  - Applies `java` automatically
  - Adds runnable/distribution tasks like `run`, `installDist`, `distZip`, and `distTar`

Java plugin relationship chart:

```text
                 java
               /      \
      java-library   application
```

Notes:

- In most projects, apply either `java-library` (for reusable libraries) or `application` (for runnable apps).
- The Java base capability is brought in automatically through the Java plugin stack.
- With core plugins, you do not specify a plugin version; Gradle uses the bundled core plugin implementation.

## Dependency note: implementation vs api

`implementation project(':util')` means:

- `app` can compile and run with `util`
- `util` is not re-exported as public API to downstream modules

Use `api` only when you intentionally want consumers to inherit that dependency.

## Dependency types and repositories

In Gradle, a dependency is a library (or module) needed by your project.

Common dependency source types:

- Module dependency (external module)
  - Published as coordinates like `group:name:version`
  - Can have multiple releases/versions
  - Usually downloaded from repositories such as Maven Central

- Other Gradle project (project dependency)
  - Depends on another module in the same multi-module build
  - Example: `implementation project(':util')`

- File dependency (not recommended for most cases)
  - Directly points to local JAR files
  - Harder to manage versions and transitive dependencies

Example repository and dependency declarations:

```gradle
repositories {
    mavenCentral()
}

dependencies {
    implementation 'com.google.guava:guava:33.2.1-jre' // module dependency
    implementation project(':util')                    // project dependency
    // implementation files('libs/legacy.jar')         // file dependency (avoid if possible)
}
```

Simple sharing model in multi-module builds:

```text
Server  --> Common
Client  --> Common
```

Both `Server` and `Client` can depend on a shared `Common` module to reuse code.

## Repository

A repository hosts published modules (artifacts) that Gradle can download.

- One repository contains many modules.
- Each module can have multiple released versions.
- Popular public repository: Maven Central.

Example repository configuration:

```gradle
repositories {
    mavenCentral()
    maven {
        url = uri("https://repo.my-company.example/maven")
    }
}
```

Use this when:

- `mavenCentral()` for common open-source dependencies.
- extra `maven { url = ... }` for internal/company artifacts.

## Useful commands

- Show modules

```bash
./gradlew projects
```

- Show all tasks

```bash
./gradlew tasks --all
```

- Run all tests

```bash
./gradlew test
```

- Run tests for one module

```bash
./gradlew :app:test
./gradlew :util:test
```

- Show dependency graph for app

```bash
./gradlew :app:dependencies --configuration compileClasspath
./gradlew :app:dependencies --configuration runtimeClasspath
```

- Explain why a dependency exists

```bash
./gradlew :app:dependencyInsight --dependency guava --configuration compileClasspath
```

## Learning mental model

- `settings.gradle` = which modules exist
- `build.gradle` = how each module is built
- `src/main/java` = real app/library code
- `src/test/java` = verification code
- `project(':util')` = module-to-module dependency

## Gradle tasks knowledge (quick)

Gradle tasks in this project come from three sources:

- Core Gradle + base lifecycle tasks
- Plugins applied in each module
- Custom task declarations in build scripts or custom task classes

In this repo specifically:

- `app/build.gradle` applies `id 'application'`
  - Adds Java-related tasks like `compileJava`, `test`, `jar`
  - Adds app tasks like `run`, `installDist`, `distZip`, `distTar`
- `util/build.gradle` applies `id 'java-library'`
  - Adds library tasks like `compileJava`, `test`, `jar`

So even without a direct `id 'java'`, this project is on the Java plugin stack through `application` and `java-library`.

How to inspect tasks:

```bash
./gradlew tasks
./gradlew tasks --all
./gradlew :app:tasks --all
./gradlew :util:tasks --all
```

Task outcomes you will commonly see:

- `UP-TO-DATE`: skipped because inputs/outputs did not change
- `FROM-CACHE`: restored from local/remote build cache
- `NO-SOURCE`: skipped because declared source inputs are empty
- `(no label)` or `EXECUTED`: task executed its actions
  - A task with actions ran those actions
  - A task with no actions may still be considered executed if it triggered dependencies (lifecycle task behavior)

## Lifecycle tasks

A lifecycle task is a task that orchestrates other tasks through dependencies and usually has no task actions of its own.

In practice:

- Running a lifecycle task triggers its dependent tasks.
- It may appear as `(no label)` or `EXECUTED` even when it has no own actions, because dependent tasks ran.
- Common lifecycle tasks include `assemble`, `check`, and `build`.

## References
- https://docs.gradle.org/current/userguide/more_about_tasks.html#sec:task_outcomes
