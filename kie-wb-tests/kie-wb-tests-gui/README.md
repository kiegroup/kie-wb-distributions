# KIE Workbench tests

This module contains selenium GUI tests.

## Running and debugging tests locally

You can run tests directly from the command line.
Cargo will take care of starting the container with kie workbench deployed and the tests will be run afterwards.

```
mvn clean verify -Pkie-wb,wildfly11
```

**Note, Selenium 2.53.0 requires Firefox 46 and is incompatible with later versions.**

Older versions can be downloaded from https://ftp.mozilla.org/pub/firefox/releases/46.0/ and tests executed as below:

```
mvn clean verify -Pkie-wb,wildfly11 -Dwebdriver.firefox.bin=/path/to/older/firefox/firefox-bin
```

For example:
```
mvn clean verify -Pkie-wb,wildfly11 -Dwebdriver.firefox.bin=/home/myuser/installs/ff46.0/firefox/firefox
```
