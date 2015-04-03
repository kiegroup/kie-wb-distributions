<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
    xmlns="http://docbook.org/ns/docbook" 
    xmlns:ns="http://docbook.org/ns/docbook" 
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://docbook.org/ns/docbook http://www.docbook.org/xml/5.0/xsd/docbook.xsd http://www.w3.org/1999/xlink http://www.docbook.org/xml/5.0/xsd/xlink.xsd"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" 
    xmlns:xlink="http://www.w3.org/1999/xlink"
    xmlns:xi="http://www.w3.org/2001/XInclude"
    xmlns:xalan="http://xml.apache.org/xslt"
    version="1.0">

  <xsl:output method="xml" indent="yes" xalan:indent-amount="4"/>
  <xsl:strip-space elements="*"/>
  <xsl:preserve-space elements="d:screen d:literallayout d:programlisting d:address"/>

  <!-- remote info and asciidoc processing instructions -->
  <xsl:template match="/ns:book/ns:info" />
  <xsl:template match="//processing-instruction()" />

  <!-- add namespaces and other info info to chapter (will become the section) element-->
  <xsl:template match="/ns:book/ns:chapter">
    <xsl:variable name="sectionId">
      <xsl:value-of select="@xml:id"/>
    </xsl:variable>

    <section version="5.0" 
         xmlns="http://docbook.org/ns/docbook" 
         xmlns:xlink="http://www.w3.org/1999/xlink"
         xml:id="{$sectionId}"
         xml:base="../"
         xsi:schemaLocation="http://docbook.org/ns/docbook http://www.docbook.org/xml/5.0/xsd/docbook.xsd http://www.w3.org/1999/xlink http://www.docbook.org/xml/5.0/xsd/xlink.xsd"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xmlns:xs="http://www.w3.org/2001/XMLSchema" 
         xmlns:xi="http://www.w3.org/2001/XInclude" 
         xmlns:ns="http://docbook.org/ns/docbook">
      <xsl:apply-templates select="@* | *" />
    </section>
  </xsl:template>
 
  <!-- remove remark element used to store xml:base information --> 
  <xsl:template match="/ns:book/ns:chapter/ns:remark[@ID='base']" />
  
  <!-- unwrap book element so that only chapter element is left over -->
  <xsl:template match="/ns:book">
    <xsl:apply-templates select="@* | *" />
  </xsl:template>

  <!-- DEBUG: warn if an element is not being processed -->
  <xsl:template match="*">
    <xsl:message terminate="no">WARNING: Unmatched element [<xsl:value-of select="name()" />]</xsl:message>
    <xsl:apply-templates />
  </xsl:template>

  <!-- keep everything: this template (rule) is called by other templates -->
  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()" />
    </xsl:copy>
  </xsl:template>
  
</xsl:stylesheet>
