Installation notes

Configure JEE security for kie-wb on tomcat (with default realm backed by tomcat-users.xml)

1. Copy kie-tomcat-integration jar into TOMCAT_HOME/lib
2. Copy jacc lib into TOMCAT_HOME/lib (javax.security.jacc-api in jboss maven repository)
3. Add valve configuration into TOMCAT_HOME/conf/server.xml inside Host element as last valve definition
<Valve className="org.kie.integration.tomcat.JACCValve" />
4. Edit TOMCAT_HOME/conf/tomcat-users.xml to include roles and users, make sure there will be kie-user roles defined as it's required to be authorized to use kie-wb
5. Edit web.xml and uncomment all entries that are marked with TOMCAT-JEE-SECURITY
6. Rename org.uberfire.security.auth.AuthenticationSource to org.uberfire.security.auth.AuthenticationSource-ORIGIN and
rename  org.uberfire.security.auth.AuthenticationSource-TOMCAT-JEE-SECURITY to org.uberfire.security.auth.AuthenticationSource
inside WEB-INF/classes/META-INF/services