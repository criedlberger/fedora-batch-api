<?xml version="1.0" encoding="UTF-8"?>
<!-- 
	University of Alberta Libraries
	Information Technology Services
	****************************************************************************
	Project: era
	Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
	Version: $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
	$Id: dcq2metadata.xsl 5430 2012-07-12 22:30:19Z pcharoen $
	
	This stylesheet transforms Dublin Core, object metadata (DC or DCQ) to metadata input form object. 
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:dc="http://purl.org/dc/elements/1.1/"
	xmlns:dcterms="http://purl.org/dc/terms/" xmlns:thesis="http://www.ndltd.org/standards/metadata/etdms/1.0/">
	<xsl:output method="xml" indent="yes" encoding="utf-8"/>
	<xsl:strip-space elements="*"/>
	<!-- DCQ to metadata -->
	<xsl:template match="/dc">
		<metadata>
			<xsl:apply-templates select="*"/>
		</metadata>
	</xsl:template>
	<!-- DC to metadata: backward compatible with DC from previous version -->
	<xsl:template match="/oai_dc:dc">
		<metadata>
			<xsl:choose>
				<xsl:when test="name() = 'dc:date'">
<!--					<xsl:apply-templates select="."/>-->
					<xsl:call-template name="dcdate">
						<xsl:with-param name="date">
							<xsl:value-of select="."/>
						</xsl:with-param>
					</xsl:call-template>
				</xsl:when>
				<xsl:when test="name() = 'dc:type'">
					<xsl:apply-templates select="."/>
				</xsl:when>
				<xsl:when test="name() = 'dc:format'">
					<xsl:apply-templates select="."/>
				</xsl:when>
				<xsl:when test="name() = 'dc:language'">
					<xsl:apply-templates select="."/>
				</xsl:when>
				<xsl:when test="name() = 'dc:identifier'">
					<xsl:apply-templates select="."/>
				</xsl:when>
				<xsl:when test="name() = 'dc:coverage'">
					<xsl:apply-templates select="."/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates select="*"/>
				</xsl:otherwise>
			</xsl:choose>
		</metadata>
	</xsl:template>
	<xsl:template match="*">
		<xsl:choose>
			<xsl:when test="count(child::*) = 0">
				<field>
					<xsl:attribute name="name">
						<xsl:value-of select="name(.)"/>
					</xsl:attribute>
					<xsl:apply-templates select="@*"/>
					<value>
						<xsl:value-of select="."/>
					</value>
				</field>
			</xsl:when>
			<xsl:otherwise>
				<xsl:for-each select="child::*">
					<field>
						<xsl:attribute name="name">
							<xsl:value-of select="name(..)"/>
						</xsl:attribute>
						<xsl:attribute name="qualifier">
							<xsl:value-of select="name(.)"/>
						</xsl:attribute>
						<xsl:apply-templates select="@*"/>
						<value>
							<xsl:value-of select="."/>
						</value>
					</field>
				</xsl:for-each>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="@*">
		<attribute>
			<xsl:attribute name="name">
				<xsl:value-of select="name()"/>
			</xsl:attribute>
			<xsl:attribute name="value">
				<xsl:value-of select="."/>
			</xsl:attribute>
		</attribute>
	</xsl:template>
	<xsl:template name="dcdate">
		<xsl:param name="date"/>
		<field name="dcterms:created">
			<value>
				<xsl:value-of select="$date"/>
			</value>
		</field>
	</xsl:template>
	<xsl:template match="dc:type">
		<field name="dc:type">
			<value>
				<xsl:value-of select="translate(., ' ()', '')"/>
			</value>
		</field>
	</xsl:template>
	<xsl:template match="dc:format">
		<field name="dc:format">
			<attribute name="xsi:type" value="dcterms:IMT"/>
			<value>
				<xsl:value-of select="."/>
			</value>
		</field>
	</xsl:template>
	<xsl:template match="dc:language">
		<field name="dc:language">
			<attribute name="xsi:type" value="dcterms:ISO639-3"/>
			<value>
				<xsl:value-of select="."/>
			</value>
		</field>
	</xsl:template>
	<xsl:template match="dc:identifier">
		<xsl:choose>
			<xsl:when test="starts-with(., 'uuid:') or starts-with(., 'http://hdl.handle.net')">
				<!-- skip uuid and handle, application will add them -->
			</xsl:when>
			<xsl:otherwise>
				<field name="dc:identifier">
					<value>
						<xsl:value-of select="."/>
					</value>
				</field>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="dc:coverage">
		<xsl:choose>
			<xsl:when test="number(substring-before(., '/'))">
				<field name="dcterms:temporal">
					<value>
						<xsl:value-of select="."/>
					</value>
				</field>
			</xsl:when>
			<xsl:otherwise>
				<field name="dcterms:spatial">
					<value>
						<xsl:value-of select="."/>
					</value>
				</field>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>