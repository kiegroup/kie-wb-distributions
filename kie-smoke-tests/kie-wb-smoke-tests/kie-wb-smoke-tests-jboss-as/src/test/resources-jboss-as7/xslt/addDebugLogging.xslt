<?xml version="1.0" encoding="UTF-8"?>
<!-- XSLT file to add the security domains to the standalone.xml used during 
	the integration tests. -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:as="urn:jboss:domain:1.2" 
	xmlns:log="urn:jboss:domain:logging:1.1" 
    exclude-result-prefixes="as log"
	version="1.0">

	<xsl:output method="xml" indent="yes" />

    <!-- add debug logging -->
    
    <xsl:template match="//as:profile/log:subsystem/log:periodic-rotating-file-handler[@name='FILE']" />
    <xsl:template match="//as:profile/log:subsystem/log:root-logger" />
    
    <xsl:template match="as:profile/log:subsystem" >
        <subsystem xmlns="urn:jboss:domain:logging:1.1">
            <periodic-rotating-file-handler name="DEBUGFILE" autoflush="true">
                <formatter>
                    <pattern-formatter>
                        <xsl:attribute name="pattern">%d{HH:mm:ss,SSS} %-5p [%c] (%t) %s%E%n</xsl:attribute>
                    </pattern-formatter>
                </formatter>
                <file relative-to="jboss.server.log.dir" path="debug.server.log"/>
                <suffix value=".yyyy-MM-dd"/>
                <append value="true"/>
            </periodic-rotating-file-handler>
            <periodic-rotating-file-handler name="FILE" autoflush="true">
                <level name="INFO"/>
                <formatter>
                    <pattern-formatter>
                        <xsl:attribute name="pattern">%d{HH:mm:ss,SSS} %-5p [%c] (%t) %s%E%n</xsl:attribute>
                    </pattern-formatter>
                </formatter>
                <file relative-to="jboss.server.log.dir" path="server.log"/>
                <suffix value=".yyyy-MM-dd"/>
                <append value="true"/>
            </periodic-rotating-file-handler>
            <root-logger>
                <level name="DEBUG"/>
                <handlers>
                    <handler name="CONSOLE"/>
                    <handler name="DEBUGFILE"/>
                    <handler name="FILE"/>
                </handlers>
            </root-logger>
            <xsl:apply-templates select="@* | *" />
        </subsystem>
    </xsl:template>
    
	<!-- Copy everything else. -->
	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" />
		</xsl:copy>
	</xsl:template>

</xsl:stylesheet>
