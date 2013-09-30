<?xml version="1.0" encoding="UTF-8"?>
<!-- 
	University of Alberta Libraries
	Information Technology Services
	****************************************************************************
	Project: era
	Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
	Version: $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
	$Id: metadata2dcq.xsl 5430 2012-07-12 22:30:19Z pcharoen $
	
	This stylesheet transforms metadata input form object to Dublin Core, object metadata (DC or DCQ). 
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/"
	xmlns:thesis="http://www.ndltd.org/standards/metadata/etdms/1.0/">
	<xsl:output method="xml" indent="yes" encoding="utf-8" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>
	<xsl:key name="qualifier" match="field[@qualifier]" use="@name"/>
	<xsl:template match="/">
		<xsl:apply-templates select="metadata"/>
	</xsl:template>
	<xsl:template match="metadata">
		<dc dc="http://purl.org/dc/elements/1.1/" xmlns:dc="http://purl.org/dc/elements/1.1/"
			xmlns:dcterms="http://purl.org/dc/terms/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
			xmlns:thesis="http://www.ndltd.org/standards/metadata/etdms/1.0/"
			xmlns:eraterms="http://era.library.ualberta.ca/eraterms"
			xsi:schemaLocation="http://www.ndltd.org/standards/metadata/etdms/1.0/ http://www.ndltd.org/standards/metadata/etdms/1.0/etdms.xsd">
			<xsl:for-each select="field[not(@qualifier) and value]">
				<xsl:sort select="@name"/>
				<xsl:element name="{@name}">
					<xsl:apply-templates select="attribute"/>
					<xsl:apply-templates select="@name"/>
				</xsl:element>
			</xsl:for-each>
			<xsl:for-each select="field[value and generate-id(.) = generate-id(key('qualifier', @name)[1])]">
				<xsl:sort select="@name"/>
				<xsl:element name="{@name}">
					<xsl:for-each select="key('qualifier', @name)">
						<xsl:if test="@qualifier and value">
							<xsl:apply-templates select="@qualifier"/>
						</xsl:if>
					</xsl:for-each>
				</xsl:element>
			</xsl:for-each>
		</dc>
	</xsl:template>
	<xsl:template match="@qualifier">
		<xsl:element name="{.}">
			<xsl:apply-templates select="../attribute"/>
			<xsl:value-of select="../value"/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="@name">
		<xsl:value-of select="../value"/>
	</xsl:template>
	<xsl:template match="attribute">
		<xsl:attribute name="{@name}">
			<xsl:value-of select="@value"/>
		</xsl:attribute>
	</xsl:template>
</xsl:stylesheet>
