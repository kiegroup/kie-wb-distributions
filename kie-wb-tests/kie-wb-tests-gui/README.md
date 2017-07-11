# KIE Workbench tests

This module contains GUI tests which require KIE Workbench to be running in order to be executed.

## Running and debugging tests locally

You can run tests directly from the command line.
Cargo will take care of starting the container and the tests will be run afterwards.

```
mvn clean verify -Pkie-wb,wildfly10
```

**Note, Selenium 2.53.0 requires Firefox 46 and is incompatible with later versions.**

Older versions can be downloaded from https://ftp.mozilla.org/pub/firefox/releases/46.0/ and tests ran as below:

```
mvn clean verify -Pkie-wb,wildfly10 -Dwebdriver.firefox.bin=/path/to/older/firefox/<firefox-bin>
```

For example:
```
mvn clean verify -Pkie-wb,wildfly10 -Dwebdriver.firefox.bin=/home/myuser/installs/ff46.0/firefox/firefox
```
