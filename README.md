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

## Dependency note: implementation vs api

`implementation project(':util')` means:

- `app` can compile and run with `util`
- `util` is not re-exported as public API to downstream modules

Use `api` only when you intentionally want consumers to inherit that dependency.

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
