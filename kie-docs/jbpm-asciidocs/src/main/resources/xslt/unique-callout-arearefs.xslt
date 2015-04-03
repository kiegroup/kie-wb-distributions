<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
    xmlns="http://docbook.org/ns/docbook" 
    xmlns:ns="http://docbook.org/ns/docbook" 
    version="2.0">

  <xsl:output method="xml" indent="yes" />

  <xsl:preserve-space elements="*" />

  <xsl:key name="co-id" match="ns:section//ns:co" use="substring-before(@xml:id,':')" />

  <xsl:template match="ns:section//ns:co">
    <xsl:variable name="orig" select="@xml:id"/>
    <xsl:copy>
      <xsl:attribute name="xml:id"> 
        <xsl:value-of select="substring-after($orig,':')"/>
      </xsl:attribute>
    </xsl:copy>
     <xsl:apply-templates select="@* | *" />
  </xsl:template>
  
  <!-- keep everything: this template (rule) is called by other templates -->
  <xsl:template match="ns:section//ns:callout" name="split">
    <xsl:param name="origRefs" select="@arearefs" />
    <xsl:param name="newList"/>
    
    <xsl:variable name="orig">
      <xsl:choose>
        <xsl:when test="contains($origRefs,' ')">
          <xsl:value-of select="substring-before($origRefs, ' ')" />
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="$origRefs" />
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:variable name="new" select="substring-after(key('co-id', $orig)/@xml:id,':')" />
    
    <xsl:choose>
      <xsl:when test="contains($origRefs,' ')">
        <xsl:call-template name="split">
          <xsl:with-param name="origRefs" select="substring-after($origRefs,' ')"/>
          <xsl:with-param name="newList" select="concat($newList, $new, ' ')" />
        </xsl:call-template>
      </xsl:when> 
      <xsl:otherwise>
        <xsl:variable name="lastNewList" select="concat($newList, $new)" />
        <xsl:copy>
          <xsl:attribute name="arearefs"> 
            <xsl:value-of select="$lastNewList"/>
          </xsl:attribute>
        </xsl:copy>
         <xsl:apply-templates select="@* | *" />
      </xsl:otherwise>
    </xsl:choose> 
    
  </xsl:template>
 
  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()" />
    </xsl:copy>
  </xsl:template>
  
</xsl:stylesheet>