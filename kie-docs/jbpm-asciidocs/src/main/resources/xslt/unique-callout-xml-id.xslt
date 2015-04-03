<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
    xmlns="http://docbook.org/ns/docbook" 
    xmlns:ns="http://docbook.org/ns/docbook" 
    version="2.0">

  <xsl:output method="xml" indent="yes" />

  <xsl:preserve-space elements="*" />
  
  <xsl:template match="ns:section//ns:co">
    <xsl:variable name="unique" select="substring(generate-id(..),4)" />
    <xsl:variable name="orig" select="@xml:id"/>
    <xsl:copy>
      <xsl:attribute name="xml:id"> 
        <xsl:value-of select="$orig" />:<xsl:value-of select="concat(substring-before($orig,'-'), $unique, '-', substring-after($orig,'-'))" /> 
      </xsl:attribute>
    </xsl:copy>
     <xsl:apply-templates select="@* | *" />
  </xsl:template>

  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()" />
    </xsl:copy>
  </xsl:template>
  
</xsl:stylesheet>