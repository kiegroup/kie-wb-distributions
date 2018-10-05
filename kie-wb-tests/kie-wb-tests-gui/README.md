# KIE Workbench GUI tests (selenium)

Before the tests run, container is started (using cargo-maven2-plugin) with kie-wb deployed.
These tests require Firefox to be available on the system.

## Running from CLI

To run the tests you have to:
- provide path to firefox binary by setting `-Dwebdriver.firefox.bin=/path/to/firefox-bin`. Selenium usually supports the latest two ESR versions (54 and 60 at the time I'm writing this).- 
- enable container profile (wildflyXX) to enable cargo configuration which starts container with kie-wb deployed

```bash
cd kie-wb-distributions/kie-wb-tests/kie-wb-tests-gui/
mvn clean verify -Pkie-wb,wildfly11 -Dwebdriver.firefox.bin=/path/to/firefox/firefox-bin
```

By default the tests are using headless firefox, so the browser window is not shown.
If you want to view the browser window as the tests are running, remove `<property name="firefoxArguments">-headless</property>` from [arquillian.xml](https://github.com/kiegroup/kie-wb-distributions/blob/master/kie-wb-tests/kie-wb-tests-gui/src/test/filtered-resources/arquillian.xml).
