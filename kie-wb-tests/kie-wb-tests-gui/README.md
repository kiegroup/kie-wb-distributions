# KIE Workbench GUI tests (selenium)

Before the tests run, container is started (using cargo maven plugin) with kie-wb deployed.
These tests require Firefox to be available on the system.

## Running from CLI

You have to provide path to firefox binary.
Selenium usually supports the latest two ESR versions (54 and 60 at the time I'm writing this).

```bash
cd kie-wb-distributions/kie-wb-tests/kie-wb-tests-gui/
mvn clean verify -Pkie-wb,wildfly11 -Dwebdriver.firefox.bin=/path/to/firefox/firefox-bin
```
