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
    version="1.0">

  <xsl:output method="xml" indent="yes" />
  <xsl:preserve-space elements="*"/>

  <!-- remote info and asciidoc processing instructions -->
  <xsl:template match="/ns:chapter/ns:info" />
  <xsl:template match="//processing-instruction()" />

  <!-- add namespaces and other info info to chapter element-->
  <xsl:template match="/ns:chapter/ns:section">
    <xsl:variable name="chapterId">
      <xsl:value-of select="@xml:id"/>
    </xsl:variable>
    <xsl:variable name="chapterBase">
      <xsl:value-of select="/ns:chapter/ns:section/ns:remark[@ID='base']"/>
    </xsl:variable>

    <chapter version="5.0" 
         xmlns="http://docbook.org/ns/docbook" 
         xmlns:xlink="http://www.w3.org/1999/xlink"
         xml:id="{$chapterId}"
         xml:base="{$chapterBase}" 
         xsi:schemaLocation="http://docbook.org/ns/docbook http://www.docbook.org/xml/5.0/xsd/docbook.xsd http://www.w3.org/1999/xlink http://www.docbook.org/xml/5.0/xsd/xlink.xsd"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xmlns:xs="http://www.w3.org/2001/XMLSchema" 
         xmlns:xi="http://www.w3.org/2001/XInclude" 
         xmlns:ns="http://docbook.org/ns/docbook">
      <xsl:apply-templates select="@* | *" />
    </chapter>
  </xsl:template>
 
  <!-- remove remark element used to store xml:base information --> 
  <xsl:template match="/ns:chapter/ns:section/ns:remark[@ID='base']" />
  
  <!-- unwrap root chapter element so that only new chapter element is left over -->
  <xsl:template match="/ns:chapter">
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
