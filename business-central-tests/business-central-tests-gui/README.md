# Business Central GUI tests (selenium)

Before the tests run, container is started (using cargo-maven2-plugin) with business-central deployed.
These tests require Firefox to be available on the system.

## Running from CLI

To run the tests you have to:
- provide path to firefox binary by setting `-Dwebdriver.firefox.bin=/path/to/firefox-bin`. Selenium usually supports the latest two ESR versions (54 and 60 at the time I'm writing this).- 
- enable container profile `wildfly` to enable cargo configuration which starts container with business-central deployed

```bash
cd business-central-distributions/business-central-tests/business-central-tests-gui/
mvn clean verify -Pbusiness-central,wildfly -Dwebdriver.firefox.bin=/path/to/firefox/firefox-bin
```

By default the tests are using headless firefox, so the browser window is not shown.
If you want to view the browser window as the tests are running, remove `<property name="firefoxArguments">-headless</property>` from [arquillian.xml](https://github.com/kiegroup/business-central-distributions/blob/master/business-central-tests/business-central-tests-gui/src/test/filtered-resources/arquillian.xml).
