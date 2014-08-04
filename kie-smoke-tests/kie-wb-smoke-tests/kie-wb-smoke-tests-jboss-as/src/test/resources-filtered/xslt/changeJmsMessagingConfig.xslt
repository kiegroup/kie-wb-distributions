<?xml version="1.0" encoding="UTF-8"?>
<!-- XSLT file to add the security domains to the standalone.xml used during 
	the integration tests. -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:as="urn:jboss:domain:1.2" 
	xmlns:msg="urn:jboss:domain:messaging:1.1" 
    exclude-result-prefixes="as msg"
	version="1.0">

	<xsl:output method="xml" indent="yes" />

    <!-- delete security-settings -->
    <xsl:template match="//as:profile/msg:subsystem/msg:hornetq-server/msg:security-settings" />

    <!-- add new security-settings with added "admin" group-->
    <xsl:template match="//as:profile/msg:subsystem/msg:hornetq-server/msg:security-settings" >
        <security-settings>
            <security-setting match="#">
                <permission type="send" roles="guest,admin"/>
                <permission type="consume" roles="guest,admin"/>
                <permission type="createNonDurableQueue" roles="guest"/>
                <permission type="deleteNonDurableQueue" roles="guest"/>
            </security-setting>
        </security-settings>
	</xsl:template>

    <!-- delete acceptor settings -->
    <!-- 
    <xsl:template match="//as:profile/msg:subsystem/msg:hornetq-server/msg:connectors/msg:netty-connector[@name='netty']" />
    <xsl:template match="//as:profile/msg:subsystem/msg:hornetq-server/msg:acceptors/msg:netty-acceptor[@name='netty']" />
     -->
     
    <xsl:template match="//as:profile/msg:subsystem/msg:hornetq-server/msg:connectors" >
        <connectors>
            <netty-connector name="netty-ssl" socket-binding="messaging-ssl">
                <param key="ssl-enabled" value="true"/>
                <param>
                    <xsl:attribute name="key">key-store-path</xsl:attribute>
                    <!-- variable expansion not working for messaging in AS 7.1.1.Final  -->
                    <xsl:attribute name="value">${project.build.directory}/${jboss.as.server.name}/standalone/configuration/ssl/keystore.jks</xsl:attribute>
                </param>
                <param key="key-store-password" value="SERVER_KEYSTORE_PASSWORD"/>
            </netty-connector>
            <xsl:apply-templates select="@* | *" />
        </connectors>
    </xsl:template>
    
    <xsl:template match="//as:profile/msg:subsystem/msg:hornetq-server/msg:acceptors" >
        <acceptors>
            <netty-acceptor name="netty-ssl" socket-binding="messaging-ssl">
                <param key="ssl-enabled" value="true"/>
                <param>
                    <xsl:attribute name="key">key-store-path</xsl:attribute>
                    <!-- variable expansion not working for messaging in AS 7.1.1.Final  -->
                    <xsl:attribute name="value">${project.build.directory}/${jboss.as.server.name}/standalone/configuration/ssl/keystore.jks</xsl:attribute>
                </param>
                <param key="key-store-password" value="SERVER_KEYSTORE_PASSWORD"/>
            </netty-acceptor>
            <xsl:apply-templates select="@* | *" />
        </acceptors>
    </xsl:template>
   
   <!-- 
    <xsl:template match="//as:profile/msg:subsystem/msg:hornetq-server/msg:jms-connection-factories/msg:connection-factory[@name='RemoteConnectionFactory']" />
     --> 
     
    <xsl:template match="//as:profile/msg:subsystem/msg:hornetq-server/msg:jms-connection-factories" >
        <jms-connection-factories>
            <connection-factory name="SslRemoteConnectionFactory">
                <connectors>
                    <connector-ref connector-name="netty-ssl"/>
                </connectors>
                <entries>
                    <entry name="java:jboss/exported/jms/SslRemoteConnectionFactory"/>
                </entries>
            </connection-factory>
            <xsl:apply-templates select="@* | *" />
        </jms-connection-factories>
    </xsl:template>
    
    <xsl:template match="//as:socket-binding-group[@name='standard-sockets']" >
        <socket-binding-group name="standard-sockets" default-interface="public" port-offset="0">
            <socket-binding name="messaging-ssl" port="5446" /> 
            <xsl:apply-templates select="@* | *" />
        </socket-binding-group>
    </xsl:template>
  
    <!-- use NIO for possibly encrypted file systems --> 
    <xsl:template match="//as:profile/msg:subsystem/msg:hornetq-server" >
        <hornetq-server>
            <journal-type>NIO</journal-type>
            <xsl:apply-templates select="@* | *" />
        </hornetq-server> 
    </xsl:template>
    
	<!-- Copy everything else. -->
	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" />
		</xsl:copy>
	</xsl:template>

</xsl:stylesheet>
