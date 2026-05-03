# Docs

## Task concepts in Gradle
- Input -> Action -> Outputs

### Input read by task
- Files
- Configuration properties
- Can be output from other tasks

### Action
- What the task does when executing

### Outputs
- Eg. Files produced by action
- Often produces are put in the `build` directory

### Depedency and orderding
- Other tasks that need to run before
- Tasks that need to run after

## Useful commands

- Get all projects
```bash
./gradlew projects
```

- Get all tasks
```bash
./gradlew tasks --all
```

- Run test suite inside app project
```bash
./gradlew app:test
```

> Note: `./gradlew test` this command will go through all subprojects and run task test for each of those subprojects. This command action was called run task at root project.

## Useful configuration

- Enable verbose console for displaying prerequisites tasks that run before our main task, edit file: `gradle.properties`
```txt
org.gradle.console=verbose
```

- Ref: [[https://docs.gradle.org/current/userguide/build_environment.html#header]](Gradle properties)
