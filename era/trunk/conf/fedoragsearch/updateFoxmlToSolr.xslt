<?xml version="1.0" encoding="UTF-8"?>
<!-- $Id: updateFoxmlToSolr.xslt 5522 2012-08-23 17:31:19Z pcharoen $ -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:exts="xalan://ca.ualberta.library.fedoragsearch.server.GenericOperationsImpl" exclude-result-prefixes="exts"
	xmlns:zs="http://www.loc.gov/zing/srw/" xmlns:foxml="info:fedora/fedora-system:def/foxml#"
	xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/"
	xmlns:dcterms="http://purl.org/dc/terms/" xmlns:eraterms="http://era.library.ualberta.ca/eraterms"
	xmlns:eraschema="http://era.library.ualberta.ca/schema/definitions.xsd#"
	xmlns:thesis="http://www.ndltd.org/standards/metadata/etdms/1.0/"
	xmlns:uvalibdesc="http://dl.lib.virginia.edu/bin/dtd/descmeta/descmeta.dtd"
	xmlns:uvalibadmin="http://dl.lib.virginia.edu/bin/admin/admin.dtd/"
	xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:model="info:fedora/fedora-system:def/model#"
	xmlns:rel="info:fedora/fedora-system:def/relations-external#" xmlns:java="http://xml.apache.org/xalan/java"
	extension-element-prefixes="java">

	<xsl:output method="xml" indent="yes" encoding="UTF-8"/>
	<!--
		This xslt stylesheet generates the Solr doc element consisting of field elements
		from a FOXML record. The PID field is mandatory.
		Options for tailoring:
		- generation of fields from other XML metadata streams than DC
		- generation of fields from other datastream types than XML
		- from datastream by ID, text fetched, if mimetype can be handled
		currently the mimetypes text/plain, text/xml, text/html, application/pdf can be handled.
	-->
	<xsl:param name="REPOSITORYNAME"/>
	<xsl:param name="FEDORASOAP"/>
	<xsl:param name="FEDORAUSER"/>
	<xsl:param name="FEDORAPASS"/>
	<xsl:param name="TRUSTSTOREPATH"/>
	<xsl:param name="TRUSTSTOREPASS"/>

	<xsl:variable name="lowercase">abcdefghijklmnopqrstuvwxyz</xsl:variable>
	<xsl:variable name="uppercase">ABCDEFGHIJKLMNOPQRSTUVWXYZ</xsl:variable>
	<xsl:variable name="PID" select="/foxml:digitalObject/@PID"/>
	<xsl:variable name="docBoost" select="1.4*2.5"/>
	<xsl:variable name="state"
		select="/foxml:digitalObject/foxml:objectProperties/foxml:property[@NAME = 'info:fedora/fedora-system:def/model#state']/@VALUE"/>
	<xsl:variable name="relsext"
		select="/foxml:digitalObject/foxml:datastream[@ID='RELS-EXT']/foxml:datastreamVersion[last()]/foxml:xmlContent"/>
	<xsl:variable name="isCommunity"
		select="boolean($relsext/rdf:RDF/rdf:Description/model:hasModel[@rdf:resource='info:fedora/ir:COMMUNITY'])"/>
	<xsl:variable name="isCollection"
		select="boolean($relsext/rdf:RDF/rdf:Description/model:hasModel[@rdf:resource='info:fedora/ir:COLLECTION'])"/>
	<xsl:variable name="eraDefinitionsUri" select="'http://era.library.ualberta.ca/schema/definitions.xsd#'"/>
	<xsl:variable name="version"
		select="/foxml:digitalObject/foxml:objectProperties/foxml:extproperty[@NAME='http://era.library.ualberta.ca/schema/definitions.xsd#version']"/>
	<xsl:variable name="developerId"
		select="/foxml:digitalObject/foxml:objectProperties/foxml:extproperty[@NAME='http://era.library.ualberta.ca/schema/definitions.xsd#developerId']"/>

	<!-- 
		Types of items:
		Embargoed Item:
			rel.isPartOf:EMBARGOED
			fo.state:Inactive
		Manual Approval Item:
			rel.isPartOf:MANUAL_APPROVAL
			fo.state:Inactive
		Dark Repository Item:
			rel.isPartOf:DARK_REPOSITORY
			fo.state:Inactive
		CCID Protected Item:
			rel.isPartOf:CCID_AUTH
			fo.state:Active
	-->
	<!-- non-public items: embargoed (state="Inactive"), dark, communities and collections -->
	<xsl:variable name="isPublic" select="starts-with($PID, 'uuid') and $state='Active'"/>

	<xsl:template match="/">
		<!-- remove this condition when rebuild index to add old data index -->
		<!--		<xsl:if test="$version and $developerId">-->
		<add>
			<doc>
				<!-- Update FedoraObjects to Solr index. -->
				<xsl:apply-templates mode="fedoraObject"/>
			</doc>
		</add>
		<!--		</xsl:if>-->
	</xsl:template>

	<xsl:template match="/foxml:digitalObject" mode="fedoraObject">
		<field name="PID">
			<xsl:value-of select="$PID"/>
		</field>
		<field name="isPublic">
			<xsl:value-of select="$isPublic"/>
		</field>
		<!-- Fedora Object Type for FOXML 1.1 -->
		<field name="fo.type">FedoraObject</field>

		<!-- Object Properties -->
		<xsl:for-each select="foxml:objectProperties/foxml:property">

			<!-- Skip type from FOXML 1.0 -->
			<xsl:if test="substring-after(@NAME, '#') != 'type'">
				<field>
					<xsl:attribute name="name">
						<xsl:choose>
							<xsl:when
								test="substring-after(@NAME, '#') = 'createdDate' or substring-after(@NAME, '#') = 'lastModifiedDate'">
								<!-- Date Fields -->
								<xsl:value-of select="concat('fo.', substring-after(@NAME, '#'), '_dt')"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="concat('fo.', substring-after(@NAME, '#'))"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:attribute>
					<xsl:value-of select="@VALUE"/>
				</field>
			</xsl:if>
		</xsl:for-each>

		<!-- Dublin Core -->
		<xsl:variable name="dcrecord"
			select="foxml:datastream/foxml:datastreamVersion[last()]/foxml:xmlContent/oai_dc:dc"/>

		<!-- DCQ -->
		<xsl:variable name="dcq" select="foxml:datastream/foxml:datastreamVersion[last()]/foxml:xmlContent/dc"/>

		<xsl:choose>
			<xsl:when test="$dcq">
				<xsl:apply-templates select="$dcq/*"/>
				<!-- DCQ sort fields -->
				<xsl:apply-templates select="$dcq/dc:title[1]" mode="sort"/>
				<xsl:apply-templates select="$dcq/dc:creator[1]" mode="sort"/>
				<xsl:apply-templates select="$dcq/dc:subject[1]" mode="sort"/>
				<xsl:choose>
					<xsl:when test="$dcq/dcterms:created">
						<xsl:apply-templates select="$dcq/dcterms:created[1]" mode="sort"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:apply-templates select="$dcq/dcterms:dateaccepted[1]" mode="sort"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select="$dcrecord/*"/>
				<!-- Dublin Core sort fields -->
				<xsl:apply-templates select="$dcrecord/dc:title[1]" mode="sort"/>
				<xsl:apply-templates select="$dcrecord/dc:creator[1]" mode="sort"/>
				<xsl:apply-templates select="$dcrecord/dc:date[1]" mode="sort"/>
				<xsl:apply-templates select="$dcrecord/dc:subject[1]" mode="sort"/>
			</xsl:otherwise>
		</xsl:choose>

		<!--
		<field name="borkcount"><xsl:value-of select="count($relsext/rdf:RDF/rdf:Description/*)"/></field>
		<field name="bork"><xsl:value-of select="$FEDORASOAP"/>|<xsl:value-of select="$FEDORAUSER"/>|<xsl:value-of select="$FEDORAPASS"/>|<xsl:value-of select="$TRUSTSTOREPATH"/>|<xsl:value-of select="$TRUSTSTOREPASS"/></field>
		-->
		<!-- Object Relationships -->
		<!--
		<xsl:variable name="fedoraHelper" select="java:org.apache.solr.handler.FedoraHelper.new($FEDORASOAP, $FEDORAUSER, $FEDORAPASS, $TRUSTSTOREPATH, $TRUSTSTOREPASS)"/>
		<xsl:variable name="fedoraAPIM" select="java:getFedoraAPIM(fedoraHelper, $TRUSTSTOREPATH, $TRUSTSTOREPASS)"/>
		-->

		<xsl:for-each select="$relsext/rdf:RDF/rdf:Description/*[not(local-name()='workflowDate')]">

			<xsl:choose>

				<!-- Data Object fo.contentModel Field -->
				<xsl:when test="name() = 'hasModel' and starts-with(@rdf:resource, 'info:fedora/ir:')">
					<field>
						<xsl:attribute name="name">fo.contentModel</xsl:attribute>
						<xsl:value-of select="substring-after(@rdf:resource, 'ir:')"/>
					</field>
				</xsl:when>

				<xsl:when test="name() = 'hasModel' and starts-with(@rdf:resource, 'info:fedora/era-')">
					<field>
						<xsl:attribute name="name">era.contentModel</xsl:attribute>
						<xsl:value-of select="substring-after(@rdf:resource, 'info:fedora/')"/>
					</field>
				</xsl:when>

				<!-- Data Object rel.isMemberOf and rel.isMemberOfCollection Field -->
				<xsl:when test="name() = 'isMemberOf' or name() = 'isMemberOfCollection'">
					<xsl:variable name="target" select="substring-after(@rdf:resource, '/')"/>
					<field name="{concat('rel.', local-name())}">
						<xsl:value-of select="$target"/>
					</field>
				<xsl:variable name="targetdc"
						select="document(concat(substring-before($FEDORASOAP, '/services'), '/get/', $target, '/DC'))"/> 
				    <!-- for testing in external environment
				    <xsl:variable name="targetdc" select="document(concat('http://era.library.ualberta.ca:8180/fedora/get/', $target, '/DC'))"/>
				    -->
					<xsl:variable name="targettitle">
						<xsl:value-of select="$targetdc/oai_dc:dc/dc:title"/>
					</xsl:variable>

					<field>
						<xsl:attribute name="name">
							<xsl:choose>
								<xsl:when test="local-name() = 'isMemberOf'">facet.community</xsl:when>
								<xsl:otherwise>facet.collection</xsl:otherwise>
							</xsl:choose>
						</xsl:attribute>
						<xsl:value-of select="$targettitle"/>
					</field>
				</xsl:when>

				<!-- Data Object rel.isPartOf Field -->
				<xsl:when test="name() = 'isPartOf'">
					<field>
						<xsl:attribute name="name">
							<xsl:value-of select="concat('rel.', name())"/>
						</xsl:attribute>
						<xsl:value-of select="substring-after(@rdf:resource, 'ir:')"/>
					</field>
					<xsl:if test="@rdf:resource = 'info:fedora/ir:CCID_AUTH'">
						<field name="era.ccid_b">true</field>
					</xsl:if>
					<xsl:if test="@rdf:resource = 'info:fedora/ir:MANUAL_APPROVAL'">
						<field name="era.approval_b">true</field>
					</xsl:if>
					<xsl:if test="@rdf:resource = 'info:fedora/ir:EMBARGOED'">
						<field name="era.embargoed_b">true</field>
					</xsl:if>
				</xsl:when>

				<!-- Data Object for Other Field -->
				<xsl:otherwise>
					<xsl:if test="namespace-uri() = $eraDefinitionsUri">
						<xsl:choose>

							<!-- Data Object for Date Field -->
							<xsl:when test="name() = 'embargoedDate' or name() = 'workflowDate'">
								<field>
									<xsl:attribute name="name">
										<xsl:value-of select="concat('era.', name(), '_dt')"/>
									</xsl:attribute>
									<xsl:value-of select="text()"/>
								</field>
							</xsl:when>

							<!-- Data Object for era.metaDescription Field -->
							<xsl:when test="name() = 'metaDescription'">
								<field>
									<xsl:attribute name="name">era.metaDescription_b</xsl:attribute>
									<xsl:value-of select="text()"/>
								</field>
							</xsl:when>

							<!-- Data Object for era.sortSER Field -->
							<xsl:when test="name() = 'sortSER'">
								<field>
									<xsl:attribute name="name">era.sortSER_b</xsl:attribute>
									<xsl:value-of select="text()"/>
								</field>
							</xsl:when>

							<!-- Data Object for era.formName Field -->
							<xsl:when test="name() = 'formName'">
								<field>
									<xsl:attribute name="name">era.formName</xsl:attribute>
									<xsl:value-of select="text()"/>
								</field>
							</xsl:when>

							<!-- Data Object for era.proquestUpload Field -->
							<xsl:when test="name() = 'proquestUpload'">
								<field>
									<xsl:attribute name="name">era.proquestUpload_b</xsl:attribute>
									<xsl:value-of select="text()"/>
								</field>
							</xsl:when>

							<!-- Data Object era.workflow Fields -->
							<xsl:when test="name() = 'workflowState'">
								<field>
									<xsl:attribute name="name">era.workflowState</xsl:attribute>
									<xsl:value-of select="text()"/>
								</field>
								<field>
									<xsl:attribute name="name">sort.workflowState</xsl:attribute>
									<xsl:choose>
										<xsl:when test=". = 'Initial'">0</xsl:when>
										<xsl:when test=". = 'Submit'">1</xsl:when>
										<xsl:when test=". = 'Cancel'">2</xsl:when>
										<xsl:when test=". = 'Review'">3</xsl:when>
										<xsl:when test=". = 'Reject'">4</xsl:when>
										<xsl:when test=". = 'Revise'">5</xsl:when>
										<xsl:when test=". = 'Archive'">6</xsl:when>
										<xsl:otherwise>0</xsl:otherwise>
									</xsl:choose>
								</field>
							</xsl:when>

							<xsl:otherwise>
								<field>
									<xsl:attribute name="name">
										<xsl:value-of select="concat('era.', name())"/>
									</xsl:attribute>
									<xsl:value-of select="text()"/>
								</field>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:if>
				</xsl:otherwise>

			</xsl:choose>
		</xsl:for-each>

	    <xsl:for-each select="$relsext/rdf:RDF/rdf:Description/eraschema:workflowDate">
            <!-- only use the latest if there is more than one -->
            <xsl:sort select="." data-type="text" order="descending"/>
            <xsl:if test="position() = 1">
                <field>
                    <xsl:attribute name="name">
                        <xsl:value-of select="concat('era.', name(), '_dt')"/>
                    </xsl:attribute>
                    <xsl:value-of select="text()"/>
                </field>
            </xsl:if>
        </xsl:for-each>
	    
		<!-- License Datastream -->
		<xsl:for-each select="foxml:datastream[@ID='LICENSE']">
			<field>
				<xsl:attribute name="name">dsm.license</xsl:attribute>
				<xsl:value-of select="foxml:datastreamVersion[last()]/@LABEL"/>
			</field>
			<field>
				<xsl:attribute name="name">license.mimeType</xsl:attribute>
				<xsl:value-of select="foxml:datastreamVersion[last()]/@MIMETYPE"/>
			</field>
			<field>
				<xsl:attribute name="name">license.url</xsl:attribute>
				<xsl:choose>
					<xsl:when test="@CONTROL_GROUP = 'X' or @CONTROL_GROUP = 'M' or starts-with($PID, 'era-license:')">
						<xsl:value-of
							select="concat(substring-before($FEDORASOAP, '/services'), '/get/', $PID, '/', @ID)"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="foxml:datastreamVersion[last()]/foxml:contentLocation/@REF"/>
					</xsl:otherwise>
				</xsl:choose>
			</field>
		</xsl:for-each>

		<!-- Thumbnail Datastream -->
		<xsl:for-each select="foxml:datastream[@ID='THUMBNAIL']/foxml:datastreamVersion[last()]">
			<field>
				<xsl:attribute name="name">dsm.thumbnail</xsl:attribute>
				<xsl:value-of select="@LABEL"/>
			</field>
			<field>
				<xsl:attribute name="name">thumbnail.mimeType</xsl:attribute>
				<xsl:value-of select="@MIMETYPE"/>
			</field>
			<field>
				<xsl:attribute name="name">thumbnail.url</xsl:attribute>
				<xsl:value-of
					select="concat(substring-before($FEDORASOAP, '/services'), '/get/', $PID, '/', 'THUMBNAIL')"/>
			</field>
		</xsl:for-each>

		<!-- Content Datastreams -->
		<!--
			a managed datastream is fetched, if its mimetype (text/plain, text/xml, text/html, application/pdf) can be handled, the text becomes the
			value of the field.
		-->
		<xsl:for-each select="foxml:datastream[starts-with(@ID, 'DS')]">
			<field>
				<xsl:attribute name="name">dsm.ids</xsl:attribute>
				<xsl:value-of select="@ID"/>
			</field>
			<field>
				<xsl:attribute name="name">dsm.controlGroups</xsl:attribute>
				<xsl:value-of select="@CONTROL_GROUP"/>
			</field>
			<field>
				<xsl:attribute name="name">dsm.labels</xsl:attribute>
				<xsl:value-of select="foxml:datastreamVersion[last()]/@LABEL"/>
			</field>
			<field>
				<xsl:attribute name="name">dsm.mimeTypes</xsl:attribute>
				<xsl:value-of select="foxml:datastreamVersion[last()]/@MIMETYPE"/>
			</field>
			<field>
				<xsl:attribute name="name">facet.format</xsl:attribute>
				<xsl:variable name="mimeTypes">
					<xsl:value-of select="foxml:datastreamVersion[last()]/@MIMETYPE"/>
				</xsl:variable>
				<xsl:choose>
					<xsl:when test="$mimeTypes = 'application/file'">Adobe PDF</xsl:when>
					<xsl:when test="$mimeTypes = 'application/ms-access'">Microsoft Access</xsl:when>
					<xsl:when test="$mimeTypes = 'application/msword'">Microsoft Word</xsl:when>
					<xsl:when test="$mimeTypes = 'application/octet-stream'">Adobe PDF</xsl:when>
					<xsl:when test="$mimeTypes = 'application/pdf'">Adobe PDF</xsl:when>
					<xsl:when test="$mimeTypes = 'application/vnd.ms-excel'">Microsoft Excel</xsl:when>
					<xsl:when test="$mimeTypes = 'application/vnd.ms-powerpoint'">Microsoft Powerpoint</xsl:when>
					<xsl:when test="$mimeTypes = 'application/msword.name'">Microsoft Word</xsl:when>
					<xsl:when test="$mimeTypes = 'application/msaccess.name'">Microsoft Access</xsl:when>
					<xsl:when
						test="$mimeTypes = 'application/vnd.openxmlformats-officedocument.presentationml.presentation'"
						>Microsoft Powerpoint</xsl:when>
					<xsl:when test="$mimeTypes = 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'"
						>Microsoft Excel</xsl:when>
					<xsl:when
						test="$mimeTypes = 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'"
						>Microsoft Word</xsl:when>
					<xsl:when
						test="$mimeTypes = 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'"
						>Microsoft Word</xsl:when>
					<xsl:when test="$mimeTypes = 'application/x-download'">Adobe PDF</xsl:when>
					<xsl:when test="$mimeTypes = 'application/x-unknown-application-pdf'">Adobe PDF</xsl:when>
					<xsl:when test="$mimeTypes = 'application/zip'">ZIP</xsl:when>
					<xsl:when test="$mimeTypes = 'audio/mpeg'">MP3</xsl:when>
					<xsl:when test="$mimeTypes = 'image/gif'">GIF</xsl:when>
					<xsl:when test="$mimeTypes = 'image/jpeg'">JPEG</xsl:when>
					<xsl:when test="$mimeTypes = 'image/pjpeg'">JPEG</xsl:when>
					<xsl:when test="$mimeTypes = 'image/tiff'">TIFF</xsl:when>
					<xsl:when test="$mimeTypes = 'text/html'">HTML</xsl:when>
					<xsl:when test="$mimeTypes = 'text/plain'">Text</xsl:when>
					<xsl:when test="$mimeTypes = 'video/quicktime'">QuickTime</xsl:when>
					<xsl:otherwise>Others</xsl:otherwise>
				</xsl:choose>
			</field>
			<field>
				<xsl:attribute name="name">dsm.urls</xsl:attribute>
				<xsl:choose>
					<xsl:when test="@CONTROL_GROUP = 'X' or @CONTROL_GROUP = 'M'">
						<xsl:value-of
							select="concat(substring-before($FEDORASOAP, '/services'), '/get/', $PID, '/', @ID)"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="foxml:datastreamVersion[last()]/foxml:contentLocation/@REF"/>
					</xsl:otherwise>
				</xsl:choose>
			</field>

			<!-- Datastream field name with index -->
			<field>
				<xsl:attribute name="name">dsm.<xsl:value-of select="position()"/>.id</xsl:attribute>
				<xsl:value-of select="@ID"/>
			</field>
			<field>
				<xsl:attribute name="name">dsm.<xsl:value-of select="position()"/>.controlGroup</xsl:attribute>
				<xsl:value-of select="@CONTROL_GROUP"/>
			</field>
			<field>
				<xsl:attribute name="name">dsm.<xsl:value-of select="position()"/>.label</xsl:attribute>
				<xsl:value-of select="foxml:datastreamVersion[last()]/@LABEL"/>
			</field>
			<field>
				<xsl:attribute name="name">dsm.<xsl:value-of select="position()"/>.mimeType</xsl:attribute>
				<xsl:value-of select="foxml:datastreamVersion[last()]/@MIMETYPE"/>
			</field>
			<field>
				<xsl:attribute name="name">dsm.<xsl:value-of select="position()"/>.location</xsl:attribute>
				<xsl:choose>
					<xsl:when test="@CONTROL_GROUP = 'X' or @CONTROL_GROUP = 'M'">
						<xsl:value-of
							select="concat(substring-before($FEDORASOAP, '/services'), '/get/', $PID, '/', @ID)"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="foxml:datastreamVersion[last()]/foxml:contentLocation/@REF"/>
					</xsl:otherwise>
				</xsl:choose>
			</field>
			<!-- 
			<field>
				<xsl:attribute name="name">dsm.contents</xsl:attribute>
				<xsl:value-of
					select="exts:getDatastreamText($PID, $REPOSITORYNAME, @ID, $FEDORASOAP, $FEDORAUSER, $FEDORAPASS, $TRUSTSTOREPATH,
				$TRUSTSTOREPASS)"
				/>
			</field>
			-->
		</xsl:for-each>
	</xsl:template>


	<xsl:template match="dc:*">
		<field>
			<xsl:attribute name="name">
				<xsl:value-of select="concat('dc.', local-name())"/>
			</xsl:attribute>
			<xsl:value-of select="text()"/>
		</field>
	</xsl:template>

	<xsl:template match="dcterms:*">
		<field>
			<xsl:attribute name="name">
				<xsl:value-of select="concat('dcterms.', local-name())"/>
			</xsl:attribute>
			<xsl:value-of select="text()"/>
		</field>
	</xsl:template>

	<xsl:template match="eraterms:*">
		<field>
			<xsl:attribute name="name">
				<xsl:value-of select="concat('eraterms.', local-name())"/>
			</xsl:attribute>
			<xsl:value-of select="text()"/>
		</field>
	</xsl:template>

	<xsl:template match="thesis:contributor">
		<field>
			<xsl:attribute name="name">
				<xsl:value-of select="concat('thesis.contributor.', @role)"/>
			</xsl:attribute>
			<xsl:value-of select="text()"/>
		</field>
	</xsl:template>

	<xsl:template match="thesis:degree/thesis:*">
		<field>
			<xsl:attribute name="name">
				<xsl:value-of select="concat('thesis.degree.', local-name())"/>
			</xsl:attribute>
			<xsl:value-of select="text()"/>
		</field>
		<xsl:if test="name() = 'thesis:discipline'">
			<field name="facet.department">
				<xsl:value-of select="."/>
			</field>
		</xsl:if>
	</xsl:template>

    <xsl:template match="dc:language">

        <xsl:variable name="code">
            <xsl:choose>
                <xsl:when test="contains(., '_')">
                    <xsl:value-of select="substring-before(., '_')"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="."/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <field name="dc.language">
            <xsl:choose>

                <!-- codes should be three-letter ISO-639-2/B (=MARC), but some 2-letter ISO-639-1 codes have crept in. 
                Full list derived from http://en.wikipedia.org/wiki/List_of_ISO_639-1_codes
                - most common three first -->
                <xsl:when test="$code='eng' or $code='en'">English</xsl:when>
                <xsl:when test="$code='fre' or $code='fr'">French</xsl:when>
                <xsl:when test="$code = 'zxx'">No Linguistic Content</xsl:when>

                <xsl:when test="$code='abk' or $code='ab'">Abkhaz</xsl:when>
                <xsl:when test="$code='aar' or $code='aa'">Afar</xsl:when>
                <xsl:when test="$code='afr' or $code='af'">Afrikaans</xsl:when>
                <xsl:when test="$code='aka' or $code='ak'">Akan</xsl:when>
                <xsl:when test="$code='alb' or $code='sq'">Albanian</xsl:when>
                <xsl:when test="$code='amh' or $code='am'">Amharic</xsl:when>
                <xsl:when test="$code='ara' or $code='ar'">Arabic</xsl:when>
                <xsl:when test="$code='arg' or $code='an'">Aragonese</xsl:when>
                <xsl:when test="$code='arm' or $code='hy'">Armenian</xsl:when>
                <xsl:when test="$code='asm' or $code='as'">Assamese</xsl:when>
                <xsl:when test="$code='ava' or $code='av'">Avaric</xsl:when>
                <xsl:when test="$code='ave' or $code='ae'">Avestan</xsl:when>
                <xsl:when test="$code='aym' or $code='ay'">Aymara</xsl:when>
                <xsl:when test="$code='aze' or $code='az'">Azerbaijani</xsl:when>
                <xsl:when test="$code='bam' or $code='bm'">Bambara</xsl:when>
                <xsl:when test="$code='bak' or $code='ba'">Bashkir</xsl:when>
                <xsl:when test="$code='baq' or $code='eu'">Basque</xsl:when>
                <xsl:when test="$code='bel' or $code='be'">Belarusian</xsl:when>
                <xsl:when test="$code='ben' or $code='bn'">Bengali</xsl:when>
                <xsl:when test="$code='bih' or $code='bh'">Bihari</xsl:when>
                <xsl:when test="$code='bis' or $code='bi'">Bislama</xsl:when>
                <xsl:when test="$code='bos' or $code='bs'">Bosnian</xsl:when>
                <xsl:when test="$code='bre' or $code='br'">Breton</xsl:when>
                <xsl:when test="$code='bul' or $code='bg'">Bulgarian</xsl:when>
                <xsl:when test="$code='bur' or $code='my'">Burmese</xsl:when>
                <xsl:when test="$code='cat' or $code='ca'">Catalan</xsl:when>
                <xsl:when test="$code='cha' or $code='ch'">Chamorro</xsl:when>
                <xsl:when test="$code='che' or $code='ce'">Chechen</xsl:when>
                <xsl:when test="$code='nya' or $code='ny'">Chichewa</xsl:when>
                <xsl:when test="$code='chi' or $code='zh'">Chinese</xsl:when>
                <xsl:when test="$code='chv' or $code='cv'">Chuvash</xsl:when>
                <xsl:when test="$code='cor' or $code='kw'">Cornish</xsl:when>
                <xsl:when test="$code='cos' or $code='co'">Corsican</xsl:when>
                <xsl:when test="$code='cre' or $code='cr'">Cree</xsl:when>
                <xsl:when test="$code='hrv' or $code='hr'">Croatian</xsl:when>
                <xsl:when test="$code='cze' or $code='cs'">Czech</xsl:when>
                <xsl:when test="$code='dan' or $code='da'">Danish</xsl:when>
                <xsl:when test="$code='div' or $code='dv'">Divehi</xsl:when>
                <xsl:when test="$code='dut' or $code='nl'">Dutch</xsl:when>
                <xsl:when test="$code='dzo' or $code='dz'">Dzongkha</xsl:when>
                <xsl:when test="$code='epo' or $code='eo'">Esperanto</xsl:when>
                <xsl:when test="$code='est' or $code='et'">Estonian</xsl:when>
                <xsl:when test="$code='ewe' or $code='ee'">Ewe</xsl:when>
                <xsl:when test="$code='fao' or $code='fo'">Faroese</xsl:when>
                <xsl:when test="$code='fij' or $code='fj'">Fijian</xsl:when>
                <xsl:when test="$code='fin' or $code='fi'">Finnish</xsl:when>
                <xsl:when test="$code='ful' or $code='ff'">Fula</xsl:when>
                <xsl:when test="$code='glg' or $code='gl'">Galician</xsl:when>
                <xsl:when test="$code='geo' or $code='ka'">Georgian</xsl:when>
                <xsl:when test="$code='ger' or $code='de'">German</xsl:when>
                <xsl:when test="$code='gre' or $code='el'">Greek</xsl:when>
                <xsl:when test="$code='grn' or $code='gn'">Guaraní</xsl:when>
                <xsl:when test="$code='guj' or $code='gu'">Gujarati</xsl:when>
                <xsl:when test="$code='hat' or $code='ht'">Haitian</xsl:when>
                <xsl:when test="$code='hau' or $code='ha'">Hausa</xsl:when>
                <xsl:when test="$code='heb' or $code='he'">Hebrew</xsl:when>
                <xsl:when test="$code='her' or $code='hz'">Herero</xsl:when>
                <xsl:when test="$code='hin' or $code='hi'">Hindi</xsl:when>
                <xsl:when test="$code='hmo' or $code='ho'">Hiri Motu</xsl:when>
                <xsl:when test="$code='hun' or $code='hu'">Hungarian</xsl:when>
                <xsl:when test="$code='ina' or $code='ia'">Interlingua</xsl:when>
                <xsl:when test="$code='ind' or $code='id'">Indonesian</xsl:when>
                <xsl:when test="$code='ile' or $code='ie'">Interlingue</xsl:when>
                <xsl:when test="$code='gle' or $code='ga'">Irish</xsl:when>
                <xsl:when test="$code='ibo' or $code='ig'">Igbo</xsl:when>
                <xsl:when test="$code='ipk' or $code='ik'">Inupiaq</xsl:when>
                <xsl:when test="$code='ido' or $code='io'">Ido</xsl:when>
                <xsl:when test="$code='ice' or $code='is'">Icelandic</xsl:when>
                <xsl:when test="$code='ita' or $code='it'">Italian</xsl:when>
                <xsl:when test="$code='iku' or $code='iu'">Inuktitut</xsl:when>
                <xsl:when test="$code='jpn' or $code='ja'">Japanese</xsl:when>
                <xsl:when test="$code='jav' or $code='jv'">Javanese</xsl:when>
                <xsl:when test="$code='kal' or $code='kl'">Kalaallisut</xsl:when>
                <xsl:when test="$code='kan' or $code='kn'">Kannada</xsl:when>
                <xsl:when test="$code='kau' or $code='kr'">Kanuri</xsl:when>
                <xsl:when test="$code='kas' or $code='ks'">Kashmiri</xsl:when>
                <xsl:when test="$code='kaz' or $code='kk'">Kazakh</xsl:when>
                <xsl:when test="$code='khm' or $code='km'">Khmer</xsl:when>
                <xsl:when test="$code='kik' or $code='ki'">Kikuyu</xsl:when>
                <xsl:when test="$code='kin' or $code='rw'">Kinyarwanda</xsl:when>
                <xsl:when test="$code='kir' or $code='ky'">Kyrgyz</xsl:when>
                <xsl:when test="$code='kom' or $code='kv'">Komi</xsl:when>
                <xsl:when test="$code='kon' or $code='kg'">Kongo</xsl:when>
                <xsl:when test="$code='kor' or $code='ko'">Korean</xsl:when>
                <xsl:when test="$code='kur' or $code='ku'">Kurdish</xsl:when>
                <xsl:when test="$code='kua' or $code='kj'">Kwanyama</xsl:when>
                <xsl:when test="$code='lat' or $code='la'">Latin</xsl:when>
                <xsl:when test="$code='ltz' or $code='lb'">Luxembourgish</xsl:when>
                <xsl:when test="$code='lug' or $code='lg'">Ganda</xsl:when>
                <xsl:when test="$code='lim' or $code='li'">Limburgish</xsl:when>
                <xsl:when test="$code='lin' or $code='ln'">Lingala</xsl:when>
                <xsl:when test="$code='lao' or $code='lo'">Lao</xsl:when>
                <xsl:when test="$code='lit' or $code='lt'">Lithuanian</xsl:when>
                <xsl:when test="$code='lub' or $code='lu'">Luba-Katanga</xsl:when>
                <xsl:when test="$code='lav' or $code='lv'">Latvian</xsl:when>
                <xsl:when test="$code='glv' or $code='gv'">Manx</xsl:when>
                <xsl:when test="$code='mac' or $code='mk'">Macedonian</xsl:when>
                <xsl:when test="$code='mlg' or $code='mg'">Malagasy</xsl:when>
                <xsl:when test="$code='may' or $code='ms'">Malay</xsl:when>
                <xsl:when test="$code='mal' or $code='ml'">Malayalam</xsl:when>
                <xsl:when test="$code='mlt' or $code='mt'">Maltese</xsl:when>
                <xsl:when test="$code='mao' or $code='mi'">Māori</xsl:when>
                <xsl:when test="$code='mah' or $code='mh'">Marshallese</xsl:when>
                <xsl:when test="$code='mon' or $code='mn'">Mongolian</xsl:when>
                <xsl:when test="$code='nau' or $code='na'">Nauru</xsl:when>
                <xsl:when test="$code='nav' or $code='nv'">Navajo</xsl:when>
                <xsl:when test="$code='nob' or $code='nb'">Norwegian Bokmål</xsl:when>
                <xsl:when test="$code='nde' or $code='nd'">North Ndebele</xsl:when>
                <xsl:when test="$code='nep' or $code='ne'">Nepali</xsl:when>
                <xsl:when test="$code='ndo' or $code='ng'">Ndonga</xsl:when>
                <xsl:when test="$code='nno' or $code='nn'">Norwegian Nynorsk</xsl:when>
                <xsl:when test="$code='nor' or $code='no'">Norwegian</xsl:when>
                <xsl:when test="$code='iii' or $code='ii'">Nuosu</xsl:when>
                <xsl:when test="$code='nbl' or $code='nr'">South Ndebele</xsl:when>
                <xsl:when test="$code='oci' or $code='oc'">Occitan</xsl:when>
                <xsl:when test="$code='oji' or $code='oj'">Ojibwe</xsl:when>
                <xsl:when test="$code='chu' or $code='cu'">Old Church Slavonic</xsl:when>
                <xsl:when test="$code='orm' or $code='om'">Oromo</xsl:when>
                <xsl:when test="$code='ori' or $code='or'">Oriya</xsl:when>
                <xsl:when test="$code='oss' or $code='os'">Ossetian</xsl:when>
                <xsl:when test="$code='pan' or $code='pa'">Panjabi</xsl:when>
                <xsl:when test="$code='pli' or $code='pi'">Pāli</xsl:when>
                <xsl:when test="$code='per' or $code='fa'">Persian</xsl:when>
                <xsl:when test="$code='pus' or $code='ps'">Pashto</xsl:when>
                <xsl:when test="$code='por' or $code='pt'">Portuguese</xsl:when>
                <xsl:when test="$code='que' or $code='qu'">Quechua</xsl:when>
                <xsl:when test="$code='roh' or $code='rm'">Romansh</xsl:when>
                <xsl:when test="$code='run' or $code='rn'">Kirundi</xsl:when>
                <xsl:when test="$code='rum' or $code='ro'">Romanian</xsl:when>
                <xsl:when test="$code='rus' or $code='ru'">Russian</xsl:when>
                <xsl:when test="$code='san' or $code='sa'">Sanskrit</xsl:when>
                <xsl:when test="$code='srd' or $code='sc'">Sardinian</xsl:when>
                <xsl:when test="$code='snd' or $code='sd'">Sindhi</xsl:when>
                <xsl:when test="$code='sme' or $code='se'">Northern Sami</xsl:when>
                <xsl:when test="$code='smo' or $code='sm'">Samoan</xsl:when>
                <xsl:when test="$code='sag' or $code='sg'">Sango</xsl:when>
                <xsl:when test="$code='srp' or $code='sr'">Serbian</xsl:when>
                <xsl:when test="$code='gla' or $code='gd'">Scottish Gaelic</xsl:when>
                <xsl:when test="$code='sna' or $code='sn'">Shona</xsl:when>
                <xsl:when test="$code='sin' or $code='si'">Sinhala</xsl:when>
                <xsl:when test="$code='slo' or $code='sk'">Slovak</xsl:when>
                <xsl:when test="$code='slv' or $code='sl'">Slovene</xsl:when>
                <xsl:when test="$code='som' or $code='so'">Somali</xsl:when>
                <xsl:when test="$code='sot' or $code='st'">Southern Sotho</xsl:when>
                <xsl:when test="$code='spa' or $code='es'">Spanish</xsl:when>
                <xsl:when test="$code='sun' or $code='su'">Sundanese</xsl:when>
                <xsl:when test="$code='swa' or $code='sw'">Swahili</xsl:when>
                <xsl:when test="$code='ssw' or $code='ss'">Swati</xsl:when>
                <xsl:when test="$code='swe' or $code='sv'">Swedish</xsl:when>
                <xsl:when test="$code='tam' or $code='ta'">Tamil</xsl:when>
                <xsl:when test="$code='tel' or $code='te'">Telugu</xsl:when>
                <xsl:when test="$code='tgk' or $code='tg'">Tajik</xsl:when>
                <xsl:when test="$code='tir' or $code='ti'">Tigrinya</xsl:when>
                <xsl:when test="$code='tib' or $code='bo'">Tibetan</xsl:when>
                <xsl:when test="$code='tuk' or $code='tk'">Turkmen</xsl:when>
                <xsl:when test="$code='tgl' or $code='tl'">Tagalog</xsl:when>
                <xsl:when test="$code='tsn' or $code='tn'">Tswana</xsl:when>
                <xsl:when test="$code='ton' or $code='to'">Tonga</xsl:when>
                <xsl:when test="$code='tur' or $code='tr'">Turkish</xsl:when>
                <xsl:when test="$code='tso' or $code='ts'">Tsonga</xsl:when>
                <xsl:when test="$code='tat' or $code='tt'">Tatar</xsl:when>
                <xsl:when test="$code='twi' or $code='tw'">Twi</xsl:when>
                <xsl:when test="$code='tah' or $code='ty'">Tahitian</xsl:when>
                <xsl:when test="$code='uig' or $code='ug'">Uighur</xsl:when>
                <xsl:when test="$code='ukr' or $code='uk'">Ukrainian</xsl:when>
                <xsl:when test="$code='urd' or $code='ur'">Urdu</xsl:when>
                <xsl:when test="$code='uzb' or $code='uz'">Uzbek</xsl:when>
                <xsl:when test="$code='ven' or $code='ve'">Venda</xsl:when>
                <xsl:when test="$code='vie' or $code='vi'">Vietnamese</xsl:when>
                <xsl:when test="$code='vkg' or $code='vk'">Viking</xsl:when>
                <xsl:when test="$code='vol' or $code='vo'">Volapük</xsl:when>
                <xsl:when test="$code='wln' or $code='wa'">Walloon</xsl:when>
                <xsl:when test="$code='wel' or $code='cy'">Welsh</xsl:when>
                <xsl:when test="$code='fry' or $code='fy'">Western Frisian</xsl:when>
                <xsl:when test="$code='xho' or $code='xh'">Xhosa</xsl:when>
                <xsl:when test="$code='yid' or $code='yi'">Yiddish</xsl:when>
                <xsl:when test="$code='yor' or $code='yo'">Yoruba</xsl:when>
                <xsl:when test="$code='zha' or $code='za'">Zhuang</xsl:when>
                <xsl:when test="$code='zul' or $code='zu'">Zulu</xsl:when>

                <xsl:when test="$code = 'other'">Other</xsl:when>
                <xsl:when test="$code = ''">Blank</xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="."/>
                </xsl:otherwise>
            </xsl:choose>
        </field>
    </xsl:template>

	<xsl:template match="dc:identifier">
		<field name="dc.identifier">
			<xsl:value-of select="."/>
		</field>
		<xsl:choose>
			<xsl:when test="starts-with(., 'uuid:')">
				<field name="id.uuid">
					<xsl:value-of select="."/>
				</field>
			</xsl:when>
			<xsl:when test="starts-with(., 'http://hdl.handle.net')">
				<field name="id.handle">
					<xsl:value-of select="."/>
				</field>
			</xsl:when>
			<xsl:otherwise>
				<field name="id.other">
					<xsl:value-of select="."/>
				</field>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:call-template name="ser"/>
		<xsl:call-template name="tr"/>
	</xsl:template>

	<xsl:template match="dcterms:identifier">
		<field name="dcterms.identifier">
			<xsl:value-of select="."/>
		</field>
		<xsl:choose>
			<xsl:when test="@xsi:type = 'eraterms:local'">
				<field name="id.uuid">
					<xsl:value-of select="."/>
				</field>
			</xsl:when>
			<xsl:when test="@xsi:type = 'dcterms:URI'">
				<field name="id.handle">
					<xsl:value-of select="."/>
				</field>
			</xsl:when>
			<xsl:otherwise>
				<field name="id.other">
					<xsl:value-of select="."/>
				</field>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:call-template name="ser"/>
		<xsl:call-template name="tr"/>
	</xsl:template>

	<xsl:template name="ser">
		<!-- START patch1 for SER ids -->
		<xsl:if test="starts-with(., 'SER')">
			<!-- Structural Engineering Reports have id in the form "SER123" -->
			<xsl:variable name="id" select="normalize-space(substring-after(., 'SER'))"/>
			<xsl:variable name="number">
				<xsl:call-template name="starting-number">
					<xsl:with-param name="s" select="$id"/>
				</xsl:call-template>
			</xsl:variable>
			<xsl:variable name="suffix" select="substring-after($id, $number)"/>
			<field name="sort.ser">
				<xsl:value-of select="format-number($number, '000000')"/>
				<xsl:value-of select="$suffix"/>
			</field>
		</xsl:if>
		<!-- END patch1 for SER ids -->
	</xsl:template>

	<!-- START patch2 for SER ids -->
	<xsl:template name="starting-number">
		<!-- recursive template to find all digits at the beginning of a string -->
		<xsl:param name="s"/>
		<xsl:variable name="init" select="substring($s, 1, 1)"/>
		<xsl:if test="translate($init, '0123456789', '') = ''">
			<!-- this is a digit -->
			<xsl:value-of select="$init"/>
			<xsl:if test="string-length($s) &gt; 1">
				<xsl:call-template name="starting-number">
					<xsl:with-param name="s" select="substring($s, 2)"/>
				</xsl:call-template>
			</xsl:if>
		</xsl:if>
	</xsl:template>
	<!-- END patch2 for SER ids -->

	<!-- TR Code template -->
	<xsl:template name="tr">
		<xsl:if test="starts-with(., 'TR')">
			<field name="sort.trid">
				<!-- years that are >= 80 are assumed to be 20th century -->
				<xsl:value-of select="substring(., 1, 2)"/>
				<xsl:choose>
					<xsl:when test="substring(., 3, 1) &gt;= 8">19</xsl:when>
					<xsl:otherwise>20</xsl:otherwise>
				</xsl:choose>
				<xsl:value-of select="substring(., 3)"/>
			</field>
		</xsl:if>
	</xsl:template>

	<xsl:template match="dc:type">
		<!-- 
			ContentModels: 
				Book
				BookChapter
				CCID
				COLLECTION
				COMMUNITY
				ConferenceWorkshopPoster
				ConferenceWorkshopPresentation
				DARK
				Dataset
				EMBARGO
				JournalArticleDraftSubmitted
				JournalArticlePublished
				LearningObject
				MANUAL
				Relationship
				Report
				ResearchMaterial
				Review
				Thesis
				TypeOfItem 
         -->
		<field name="dc.type">
			<xsl:choose>
				<xsl:when test=". = 'BookChapter'">Book Chapter</xsl:when>
				<xsl:when test=". = 'ConferenceWorkshopPoster'">Conference Workshop Poster</xsl:when>
				<xsl:when test=". = 'Conference/workshop Poster'">Conference Workshop Poster</xsl:when>
				<xsl:when test=". = 'ConferenceWorkshopPresentation'">Conference Workshop Presentation</xsl:when>
				<xsl:when test=". = 'Conference/workshop Presentation'">Conference Workshop Presentation</xsl:when>
				<xsl:when test=". = 'JournalArticleDraftSubmitted'">Journal Article Draft Submitted</xsl:when>
				<xsl:when test=". = 'JournalArticlePublished'">Journal Article Published</xsl:when>
				<xsl:when test=". = 'LearningObject'">Learning Object</xsl:when>
				<xsl:when test=". = 'ResearchMaterial'">Research Material</xsl:when>
				<xsl:otherwise>
					<!-- capitalize -->
					<xsl:value-of select="translate(substring(., 1, 1), $lowercase, $uppercase)"/>
					<xsl:value-of select="substring(., 2)"/>
				</xsl:otherwise>
			</xsl:choose>
		</field>

	</xsl:template>

	<xsl:template match="dc:subject">
		<!-- some subjects aren't capitalized -->
		<field name="dc.subject">
			<xsl:value-of select="translate(substring(., 1, 1), $lowercase, $uppercase)"/>
			<xsl:value-of select="substring(., 2)"/>
		</field>
	</xsl:template>

	<xsl:template match="dcterms:dateaccepted|dcterms:datesubmitted">
		<field>
			<xsl:attribute name="name">
				<xsl:value-of select="concat('dcterms.', local-name(), '_dt')"/>
			</xsl:attribute>
			<xsl:value-of select="."/>
		    <!-- handle case of manually edited dates in form "2012-04-05", which cause Solr to reject the record -->
		    <xsl:if test="string-length(.) = 10">T19:00:00Z</xsl:if>
		</field>
	</xsl:template>

	<!-- Sort fields -->
	<xsl:template match="dc:title" mode="sort">
		<xsl:variable name="lapos">L'</xsl:variable>
		<xsl:variable name="apos">'</xsl:variable>
		<xsl:variable name="quot">"</xsl:variable>
		<xsl:variable name="normtitle" select="normalize-space(.)"/>
		<xsl:variable name="rawtitle">
			<xsl:choose>
				<xsl:when test="starts-with($normtitle, $apos) or starts-with($normtitle, $quot)">
					<xsl:value-of select="substring($normtitle, 2)"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$normtitle"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<field name="sort.title">
			<xsl:choose>
				<xsl:when test="starts-with($rawtitle, 'The ')">
					<xsl:value-of select="substring($rawtitle, 5)"/>
				</xsl:when>
				<xsl:when test="starts-with($rawtitle, 'A ')">
					<xsl:value-of select="substring($rawtitle, 3)"/>
				</xsl:when>
				<xsl:when test="starts-with($rawtitle, 'An ')">
					<xsl:value-of select="substring($rawtitle, 3)"/>
				</xsl:when>
				<xsl:when test="starts-with($rawtitle, 'Le ')">
					<xsl:value-of select="substring($rawtitle, 4)"/>
				</xsl:when>
				<xsl:when test="starts-with($rawtitle, 'La ')">
					<xsl:value-of select="substring($rawtitle, 4)"/>
				</xsl:when>
				<xsl:when test="starts-with($rawtitle, 'Les ')">
					<xsl:value-of select="substring($rawtitle, 5)"/>
				</xsl:when>
				<xsl:when test="starts-with($rawtitle, $lapos)">
					<xsl:value-of select="substring($rawtitle, 3)"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$rawtitle"/>
				</xsl:otherwise>
			</xsl:choose>
		</field>
	</xsl:template>

	<xsl:template match="dc:creator" mode="sort">
		<field name="sort.author">
			<xsl:value-of select="."/>
		</field>
	</xsl:template>
	<xsl:template match="dc:date" mode="sort">
		<field name="sort.date">
			<xsl:value-of select="."/>
		</field>
	</xsl:template>
	<xsl:template match="dc:subject" mode="sort">
		<field name="sort.subject">
			<xsl:value-of select="."/>
		</field>
	</xsl:template>
	<xsl:template match="dcterms:created" mode="sort">
		<field name="sort.date">
			<xsl:value-of select="."/>
		</field>
	</xsl:template>
	<xsl:template match="dcterms:dateaccepted" mode="sort">
		<field name="sort.date">
			<xsl:value-of select="."/>
		</field>
	</xsl:template>
</xsl:stylesheet>
