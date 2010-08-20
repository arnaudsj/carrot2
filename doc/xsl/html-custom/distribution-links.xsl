<?xml version='1.0'?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:product="http://www.carrot2.org"
                xmlns:d="http://docbook.org/ns/docbook"
                version="1.0" exclude-result-prefixes="product">
                
  <xsl:param name="dist.url" />
  <xsl:param name="product.version" />
  <xsl:param name="product.demo.url" />
  
  <xsl:param name="product.java-api.base" />
  <xsl:param name="product.csharp-api.base" />
  <xsl:param name="product.dcs.base" />
  <xsl:param name="product.webapp.base" />
  <xsl:param name="product.cli.base" />
  <xsl:param name="product.workbench.base" />
  <xsl:param name="product.manual.base" />
  <xsl:param name="carrot2.javadoc.url" />
  
  <xsl:template match="product:java-api-download-link">
    <a href="{$dist.url}/{$product.java-api.base}-{$product.version}.zip"><xsl:apply-templates /></a>
  </xsl:template>
    
  <xsl:template match="product:csharp-api-download-link">
    <a href="{$dist.url}/{$product.csharp-api.base}-{$product.version}.zip"><xsl:apply-templates /></a>
  </xsl:template>
    
  <xsl:template match="product:dcs-download-link">
    <a href="{$dist.url}/{$product.dcs.base}-{$product.version}.zip"><xsl:apply-templates /></a>
  </xsl:template>  

  <xsl:template match="product:cli-download-link">
    <a href="{$dist.url}/{$product.cli.base}-{$product.version}.zip"><xsl:apply-templates /></a>
  </xsl:template>  

  <xsl:template match="product:webapp-download-link">
    <a href="{$dist.url}/{$product.webapp.base}-{$product.version}.war"><xsl:apply-templates /></a>
  </xsl:template>  

  <xsl:template match="product:workbench-download-link">
    <a href="{$dist.url}/{$product.workbench.base}-{@os}.{@wm}.x86-{$product.version}.zip"><xsl:apply-templates /></a>
  </xsl:template>
  
  <xsl:template match="product:online-demo-link">
    <xsl:variable name="content">
      <xsl:choose>
        <xsl:when test="string-length(.) > 0">
          <xsl:apply-templates />
        </xsl:when>
        
        <xsl:otherwise><xsl:value-of select="$product.demo.url" /></xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <a href="{$product.demo.url}"><xsl:value-of select="$content" /></a>
  </xsl:template>
  
  <xsl:template match="product:version"><xsl:value-of select="$product.version" /></xsl:template>
  
  <xsl:template match="d:link[@role = 'javadoc']">
    <xsl:variable name="class-name">
      <xsl:call-template name="from-last-substring">
        <xsl:with-param name="string" select="@linkend" />
        <xsl:with-param name="substring" select="'.'" />
      </xsl:call-template>
    </xsl:variable>
    <a href="{$carrot2.javadoc.url}/{translate(@linkend, '.$', '/.')}.html"><xsl:value-of select="$class-name" /></a>
  </xsl:template>
  
  <xsl:template name="from-last-substring">
    <xsl:param name="string" />
    <xsl:param name="substring" />
    
    <xsl:choose>
      <xsl:when test="contains($string, $substring)">
        <xsl:call-template name="from-last-substring">
          <xsl:with-param name="string" select="substring-after($string, $substring)" />
          <xsl:with-param name="substring" select="$substring" />
        </xsl:call-template>
      </xsl:when>
      
      <xsl:otherwise><xsl:value-of select="$string" /></xsl:otherwise>
    </xsl:choose>
  </xsl:template>
</xsl:stylesheet>

