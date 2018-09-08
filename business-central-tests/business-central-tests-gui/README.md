# Business Central tests

Before the tests run, container is started (using cargo-maven2-plugin) with kie-wb deployed.
These tests require Firefox to be available on the system.

## Running from CLI

To run the tests you have to:
- provide path to firefox binary by setting `-Dwebdriver.firefox.bin=/path/to/firefox-bin`. Selenium usually supports the latest two ESR versions (54 and 60 at the time I'm writing this).- 
- enable container profile (wildflyXX) to enable cargo configuration which starts container with kie-wb deployed

```
mvn clean verify -Pbusiness-central,wildfly11
```

**Note, Selenium 2.53.0 requires Firefox 46 and is incompatible with later versions.**

Older versions can be downloaded from https://ftp.mozilla.org/pub/firefox/releases/46.0/ and tests executed as below:

```
mvn clean verify -Pbusiness-central,wildfly11 -Dwebdriver.firefox.bin=/path/to/older/firefox/firefox-bin
```

For example:
```
mvn clean verify -Pbusiness-central,wildfly11 -Dwebdriver.firefox.bin=/home/myuser/installs/ff46.0/firefox/firefox
```
