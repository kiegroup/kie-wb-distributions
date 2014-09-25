<?xml version="1.0" encoding="UTF-8"?>
<!-- XSLT file to add the security domains to the standalone.xml used during 
	the integration tests. -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:as="urn:jboss:domain:1.6" 
	xmlns:dat="urn:jboss:domain:datasources:1.2" 
    exclude-result-prefixes="as dat"
	version="1.0">

	<xsl:output method="xml" indent="yes" />

  <!-- add new security-settings with added "admin" group-->
  <xsl:template match="//as:profile/dat:subsystem/dat:datasources" >
      <datasources>
          <datasource jndi-name="java:jboss/datasources/ExampleDS" pool-name="ExampleDS" enabled="true" use-java-context="true">
              <connection-url>jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;MVCC=TRUE</connection-url>
              <driver>h2</driver>
              <security>
                  <user-name>sa</user-name>
                  <password>sa</password>
              </security>
          </datasource>
          <datasource jndi-name="java:jboss/datasources/jbpmDS" jta="true" pool-name="jbpmDS" enabled="true" use-java-context="true" use-ccm="true">
              <connection-url>jdbc:postgresql://localhost:5432/jbpm5</connection-url>
              <driver>org.postgresql</driver>
              <pool>
                  <min-pool-size>2</min-pool-size>
                  <max-pool-size>20</max-pool-size>
                  <prefill>true</prefill>
              </pool>
              <security>
                  <user-name>jbpm5</user-name>
                  <password>jbpm5</password>
              </security>
              <validation>
                  <check-valid-connection-sql>SELECT 1</check-valid-connection-sql>
              </validation>
          </datasource>
          <drivers>
              <driver name="h2" module="com.h2database.h2">
                  <xa-datasource-class>org.h2.jdbcx.JdbcDataSource</xa-datasource-class>
              </driver>
              <driver name="org.postgresql" module="org.postgresql">
                  <xa-datasource-class>org.postgresql.xa.PGXADataSource</xa-datasource-class>
              </driver>
          </drivers>
      </datasources>
	</xsl:template>

	<!-- Copy everything else. -->
	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" />
		</xsl:copy>
	</xsl:template>

</xsl:stylesheet>
