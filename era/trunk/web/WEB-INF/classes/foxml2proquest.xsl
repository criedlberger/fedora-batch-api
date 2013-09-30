<?xml version="1.0" encoding="UTF-8"?>
<!-- 
	University of Alberta Libraries
	Information Technology Services
	****************************************************************************
	Project: era
	Author: Piyapong Charoenwattana (piyapong.charoenwattana@ualberta.ca)
	Version: $Revision: 1181 $ $Date: 2012-07-11 15:46:27 -0600 (Wed, 11 Jul 2012) $
	$Id: foxml2proquest.xsl 1181 2012-07-11 21:46:27Z pcharoen $
	
	This stylesheet transforms Fedora Object XML (FOXML) to Proquest upload XML metadata. 
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/"
	xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:eraterms="http://era.library.ualberta.ca/eraterms"
	xmlns:thesis="http://www.ndltd.org/standards/metadata/etdms/1.0/" xmlns:foxml="info:fedora/fedora-system:def/foxml#"
	exclude-result-prefixes="foxml dc oai_dc dcterms thesis eraterms" xmlns:xslt="http://xml.apache.org/xslt">
	<xsl:output method="xml" indent="yes" encoding="utf-8" omit-xml-declaration="yes" xslt:indent-amount="4" />
	<xsl:strip-space elements="*" />
	<xsl:param name="externalId" />
	<xsl:param name="instcode" />
	<xsl:variable name="dcq" select="/foxml:digitalObject/foxml:datastream[@ID='DCQ']/foxml:datastreamVersion[last()]/foxml:xmlContent/dc" />
	<xsl:variable name="datastreams" select="/foxml:digitalObject/foxml:datastream[starts-with(@ID,'DS')]" />
	<xsl:template match="/">
		<DISS_submission publishing_option="0" embargo_code="0" third_party_search="Y">
			<xsl:apply-templates select="$dcq" />
			<xsl:apply-templates select="$datastreams" />
			<DISS_content>
				<xsl:for-each select="$datastreams">
					<xsl:apply-templates select="foxml:datastreamVersion[last()]" />
				</xsl:for-each>
			</DISS_content>
			<xsl:apply-templates select="$dcq/thesis:contributor" />
			<DISS_restriction>
				<DISS_sales_restriction />
				<DISS_format_restriction />
			</DISS_restriction>
		</DISS_submission>
	</xsl:template>
	<xsl:template match="dc">
		<xsl:apply-templates select="dc:creator" />
		<DISS_description page_count="00" type="masters" apply_for_copyright="no">
			<xsl:attribute name="external_id"><xsl:value-of select="$externalId" /></xsl:attribute>
			<xsl:apply-templates select="dc:title" />
			<DISS_dates>
				<xsl:apply-templates select="eraterms:graduationdate" />
				<xsl:apply-templates select="dcterms:dateaccepted" />
			</DISS_dates>
			<xsl:apply-templates select="thesis:degree" />
			<DISS_categorization>
				<DISS_category>
					<DISS_cat_code />
					<DISS_cat_desc />
				</DISS_category>
				<xsl:apply-templates select="dc:subject" />
				<xsl:apply-templates select="dc:language" />
			</DISS_categorization>
		</DISS_description>
	</xsl:template>
	<xsl:template match="foxml:datastreamVersion[last()]">
		<xsl:choose>
			<xsl:when test="starts-with(@ID, 'DS1')">
				<DISS_abstract>
					<DISS_para>
						<xsl:value-of select="$dcq/dcterms:abstract" />
					</DISS_para>
				</DISS_abstract>
				<DISS_binary type="PDF"><xsl:value-of select="substring-before($dcq/dc:creator, ',')" />_<xsl:value-of select="substring-after($dcq/dc:creator, ', ')" />.pdf</DISS_binary>
				<DISS_plaintext>
					<DISS_para />
				</DISS_plaintext>
			</xsl:when>
			<xsl:otherwise>
				<DISS_attachment>
					<DISS_file_name>
						<xsl:value-of select="@LABEL" />
					</DISS_file_name>
					<DISS_file_descr />
				</DISS_attachment>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="dc:creator">
		<DISS_authorship>
			<DISS_author type="primary">
				<DISS_name>
					<DISS_surname>
						<xsl:value-of select="substring-before(., ',')" />
					</DISS_surname>
					<DISS_fname>
						<xsl:value-of select="substring-after(., ', ')" />
					</DISS_fname>
				</DISS_name>
				<DISS_contact>
					<DISS_contact_effdt />
					<DISS_address>
						<DISS_addrline />
						<DISS_city />
						<DISS_pcode />
						<DISS_country />
					</DISS_address>
				</DISS_contact>
			</DISS_author>
		</DISS_authorship>
	</xsl:template>
	<xsl:template match="thesis:contributor">
		<xsl:choose>
			<xsl:when test="@role = 'advisor'">
				<DISS_advisor>
					<xsl:call-template name="contributor" />
				</DISS_advisor>
			</xsl:when>
			<xsl:when test="@role = 'committeemember'">
				<DISS_cmte_member>
					<xsl:call-template name="contributor" />
				</DISS_cmte_member>
			</xsl:when>
		</xsl:choose>
	</xsl:template>
	<xsl:template name="contributor">
		<DISS_name>
			<DISS_surname>
				<xsl:value-of select="substring-before(., ',')" />
			</DISS_surname>
			<DISS_fname>
				<xsl:variable name="lastname" select="substring-after(., ', ')" />
				<xsl:value-of select="substring-before($lastname, ' (')" />
			</DISS_fname>
			<DISS_affiliation>
				<xsl:variable name="department" select="substring-after(., '(')" />
				<xsl:value-of select="substring-before($department, ')')" />
			</DISS_affiliation>
		</DISS_name>
	</xsl:template>
	<xsl:template match="dc:title">
		<DISS_title>
			<xsl:value-of select="." />
		</DISS_title>
	</xsl:template>
	<xsl:template match="eraterms:graduationdate">
		<DISS_comp_date>
			<xsl:value-of select="substring-before(., '-')" />
		</DISS_comp_date>
	</xsl:template>
	<xsl:template match="dcterms:dateaccepted">
		<DISS_accept_date>
			<xsl:value-of select="substring(., 6, 2)" />/<xsl:value-of select="substring(., 9, 2)" />/<xsl:value-of select="substring(., 1, 4)" />
		</DISS_accept_date>
	</xsl:template>
	<xsl:template match="thesis:degree">
		<DISS_degree>
			<xsl:apply-templates select="thesis:name" />
		</DISS_degree>
		<DISS_institution>
			<DISS_inst_code><xsl:value-of select="$instcode" /></DISS_inst_code>
			<DISS_inst_name>
				<xsl:value-of select="thesis:grantor" />
			</DISS_inst_name>
			<DISS_inst_contact>
				<xsl:value-of select="thesis:discipline" />
			</DISS_inst_contact>
		</DISS_institution>
	</xsl:template>
	<xsl:template match="dc:subject">
		<DISS_keyword>
			<xsl:value-of select="." />
		</DISS_keyword>
	</xsl:template>
	<xsl:template match="dc:language">
		<DISS_language>
			<xsl:choose>
				<xsl:when test=". = 'eng'">EN</xsl:when>
				<xsl:when test=". = 'fre'">FR</xsl:when>
				<xsl:when test=". = 'ger'">DE</xsl:when>
				<xsl:when test=". = 'spa'">ES</xsl:when>
				<xsl:otherwise>EN</xsl:otherwise>
			</xsl:choose>
		</DISS_language>
	</xsl:template>
	<xsl:template match="thesis:name">
		<xsl:choose>
			<xsl:when test=". = 'Master of Arts'">M.A</xsl:when>
			<xsl:when test=". = 'Master of Arts/Master of Library and Information Studies'">M.A./M.L.I.S.</xsl:when>
			<xsl:when test=". = 'Master of Business Administration'">M.B.A</xsl:when>
			<xsl:when test=". = 'Master of Education'">M.Ed.</xsl:when>
			<xsl:when test=". = 'Master of Laws'">LL.M.</xsl:when>
			<xsl:when test=". = 'Master of Library and Information Studies'">M.L.I.S.</xsl:when>
			<xsl:when test=". = 'Master of Nursing'">M.N.</xsl:when>
			<xsl:when test=". = 'Master of Science'">M.S.</xsl:when>
			<xsl:when test=". = 'Doctor of Education'">Dr.Ed.</xsl:when>
			<xsl:when test=". = 'Doctor of Music'">Mus.D.</xsl:when>
			<xsl:when test=". = 'Doctor of Philosophy'">Ph.D.XXX</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="." />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="*" />
</xsl:stylesheet>
