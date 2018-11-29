# Advent Of Code 2018

Template project for Kotlin tests and solutions for the [2018 puzzles](http://adventofcode.com/2018).

To get started, simply fork this project! Then:

- Copy your daily inputs into the `dayXX/src/main/resources/input.txt` files.
- Write your daily solutions in the `main` functions in the files named `dayXX/src/main/kotlin/Main.kt`.
- (Optional) Write your daily tests by creating new test classes in the `dayXX/src/test/kotlin/` directories.

This project uses:
- Kotlin 1.3.10
- Gradle 5.0
- KotlinTest 3.1.10

# Instructions

## Running Solutions

All days:

    ./gradlew run

Specific day, e.g. 14:

    ./gradlew day14:run

## Running Tests

All days:

    ./gradlew test

Specific day, e.g. 14:

    ./gradlew day14:test
