How to test and run

gradle test run

By default, ./data is working directory.
Application reads data from this directory.

To change working dir, change settings in build.gradle

run {
    args = ["./data"]
}















