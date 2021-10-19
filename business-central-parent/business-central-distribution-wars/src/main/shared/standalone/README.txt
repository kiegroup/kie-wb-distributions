To simply start the jar type:
java -jar <jar-name>

To setup an user and some system properties run:
java -jar business-central-standalone.jar --cli-script=application-script.cli

To startup the jar when using the file system realm (default user realm) run:
java -jar business-central-standalone.jar -Dorg.uberfire.ext.security.management.wildfly.cli.folderPath=kie-fs-realm-users