<?xml version="1.0" encoding="UTF-8"?>

<!--
	date: 2002/01/30
	version: dm-2.0a14-pre6

	stylesheet for the transformation of
	XML-exported topicmaps to formatting objects
-->
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format"
	xmlns:fox="http://xml.apache.org/fop/extensions"
	xmlns:svg="http://www.w3.org/2000/svg">

	<xsl:output method="xml" indent="yes"/>



	<!-- stylesheet imports -->



	<!-- import HTML -> FO stylesheet -->
	<xsl:include href="stylesheets/html2fo.xsl"/>



	<!-- top-level parameters, submitted by the XSL processor -->



	<!-- directory which contains topic icons -->
	<xsl:param name="iconDirectory" select="'iconDirectory parameter not submitted by XSL processor'"/>



	<!-- global variables -->



	<!-- colors -->
	<xsl:variable name="color-head-foot" select="'#880000'"/>
	<xsl:variable name="color-topic-head" select="'#880000'"/>
	<xsl:variable name="color-refs-props" select="'#D8D8C8'"/>
	<xsl:variable name="color-label-left" select="'#E8E8D8'"/>
	<xsl:variable name="color-label-right" select="'#E8E8D8'"/>
	<xsl:variable name="color-field-left" select="'#F8F8E8'"/>
	<xsl:variable name="color-field-right" select="'#F8F8E8'"/>
	<xsl:variable name="color-hyperlink" select="'#880000'"/>



	<!-- templates -->



	<xsl:template match="/">
		<fo:root>
			<fo:layout-master-set>
				<fo:simple-page-master master-name="A4"
				page-height="29.7cm" page-width="21cm"
				margin-top="1cm" margin-bottom="2cm"
				margin-left="2.5cm" margin-right="2cm"
				widows="2" orphans="2">
					<fo:region-before extent="1cm"/>
					<fo:region-body margin-top="1cm" margin-bottom="1cm"/>
					<fo:region-after extent="1cm"/>
				</fo:simple-page-master>
			</fo:layout-master-set>
			<fo:page-sequence master-reference="A4" initial-page-number="1">
				<!-- header -->
				<fo:static-content flow-name="xsl-region-before">
	          <fo:block font-style="italic">
						<fo:table>
							<fo:table-column column-width="4.5cm"/>
							<fo:table-column column-width="12cm"/>
							<fo:table-body>
								<fo:table-row>
									<fo:table-cell>
										<fo:block>
											<xsl:call-template name="expandTopicTypeName">
												<xsl:with-param name="id" select="/topicmap/@type"/>
											</xsl:call-template>
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block text-align="end">
											<xsl:value-of select="/topicmap/@name"/>
										</fo:block>
									</fo:table-cell>
								</fo:table-row>
							</fo:table-body>
						</fo:table>
					</fo:block>
					<fo:block>
					  <fo:leader leader-length="100%" leader-pattern="rule"
	             rule-thickness="2pt" color="{$color-head-foot}"/>
					</fo:block>
	        </fo:static-content>
				<!-- footer -->
	        <fo:static-content flow-name="xsl-region-after">
					<fo:block>
					  <fo:leader leader-length="100%" leader-pattern="rule"
					             rule-thickness="2pt" color="{$color-head-foot}"/>
					</fo:block>
	          <fo:block text-align="end" font-style="italic">Seite <fo:page-number/> von <fo:page-number-citation ref-id="last"/></fo:block>
	        </fo:static-content>
				<!-- text flow with hyphenation (german) -->
				<fo:flow flow-name="xsl-region-body" hyphenate="true" language="de">
					<fo:block space-before="3pt">
						<fo:table border-collapse="separate">
							<fo:table-column column-width="5cm"/>
							<fo:table-column column-width="0.5cm"/>
							<fo:table-column column-width="11cm"/>
							<fo:table-body>
								<!-- map title -->
								<fo:table-row>
									<fo:table-cell number-columns-spanned="3" padding-left="5pt" padding-right="5pt" background-color="{$color-topic-head}" border-width="2pt" border-color="white" border-style="solid">
										<fo:block color="white" font-weight="bold" font-size="1.2em">
											<xsl:call-template name="expandTopicTypeName">
												<xsl:with-param name="id" select="/topicmap/@type"/>
											</xsl:call-template>
											<xsl:text>: "</xsl:text>
											<xsl:value-of select="/topicmap/@name"/>
											<xsl:text>"</xsl:text>
										</fo:block>
									</fo:table-cell>
								</fo:table-row>
								<!-- render map properties, if present -->
								<xsl:if test="/topicmap/property">
									<xsl:call-template name="render_spanned_columns">
										<xsl:with-param name="text" select="'Eigenschaften dieser TopicMap'"/>
									</xsl:call-template>
									<xsl:call-template name="render_legend">
										<xsl:with-param name="left" select="'Eigenschaft'"/>
										<xsl:with-param name="right" select="'Wert'"/>
									</xsl:call-template>
									<xsl:apply-templates select="/topicmap/property">
										<xsl:sort select="@name"/>
									</xsl:apply-templates>
								</xsl:if>
								<!-- SVG figure -->
								<xsl:call-template name="render_spanned_columns">
									<xsl:with-param name="text" select="'Grafische Darstellung dieser TopicMap'"/>
								</xsl:call-template>
								<fo:table-row>
									<fo:table-cell number-columns-spanned="3" border-width="2pt" border-color="white" border-style="solid">
										<fo:block>
											<fo:instream-foreign-object>
												<!-- marker for the SAX processor to insert the SVG graphics here -->
												<xsl:processing-instruction name="insert">svg</xsl:processing-instruction>
											</fo:instream-foreign-object>
										</fo:block>
									</fo:table-cell>
								</fo:table-row>
								<!-- list of topics visible in the map -->
								<xsl:call-template name="render_spanned_columns">
									<xsl:with-param name="text" select="'In dieser TopicMap vorhandene Topics'"/>
								</xsl:call-template>
								<xsl:call-template name="render_legend">
									<xsl:with-param name="left" select="'Topictyp'"/>
									<xsl:with-param name="right" select="'Topic'"/>
								</xsl:call-template>
								<xsl:apply-templates select="/topicmap/topic" mode="table">
									<xsl:sort select="@types"/>
								</xsl:apply-templates>
							</fo:table-body>
						</fo:table>
					</fo:block>
					<!-- now render the topics -->
					<fo:block space-before="5pt" break-before="page">
						<xsl:apply-templates select="/topicmap/topic">
							<xsl:sort select="@types"/>
						</xsl:apply-templates>
					</fo:block>
					<!-- marker for last page (= number of pages) -->
					<fo:block id="last"/>
				</fo:flow>
			</fo:page-sequence>
			<!-- FOP extension: PDF bookmarks -->
			<xsl:for-each select="/topicmap/topic">
				<xsl:sort select="@types"/>
				<xsl:variable name="ID" select="@ID"/>
				<fox:outline internal-destination="{$ID}">
					<fox:label>
						<xsl:call-template name="expandTopicTypeName">
							<xsl:with-param name="id" select="@types"/>
						</xsl:call-template>
						<xsl:text>: "</xsl:text>
						<xsl:apply-templates select="topname/basename"/>
						<xsl:text>"</xsl:text>
					</fox:label>
				</fox:outline>
			</xsl:for-each>
		</fo:root>
	</xsl:template>

	<!-- render a topic as hyperlink -->
	<xsl:template match="topic" mode="link">
		<xsl:variable name="ID" select="@ID"/>
		<xsl:call-template name="expandTopicIcon">
			<xsl:with-param name="topic" select="."/>
		</xsl:call-template>
		<xsl:text>  </xsl:text>
		<fo:basic-link internal-destination="{$ID}" color="{$color-hyperlink}">
			<xsl:apply-templates select="topname/basename"/> (Seite <fo:page-number-citation ref-id="{$ID}"/>)
		</fo:basic-link>
	</xsl:template>

	<!-- render a topic in table layout -->
	<xsl:template match="topic">
		<xsl:variable name="ID" select="@ID"/>
		<fo:block space-before="10pt" space-after="10pt">
			<!-- table start-->
			<fo:block space-before="3pt">
				<fo:table border-collapse="separate">
					<fo:table-column column-width="5cm"/>
					<fo:table-column column-width="0.5cm"/>
					<fo:table-column column-width="11cm"/>
					<fo:table-body>
						<!-- table header -->
						<fo:table-row>
							<fo:table-cell number-columns-spanned="3" padding-left="5pt" padding-right="5pt" background-color="{$color-topic-head}" border-width="2pt" border-color="white" border-style="solid">
								<fo:block color="white" font-weight="bold" font-size="1.2em" id="{$ID}">
									<xsl:call-template name="expandTopicIcon">
										<xsl:with-param name="topic" select="."/>
									</xsl:call-template>
									<xsl:text>  </xsl:text>
									<xsl:call-template name="expandTopicTypeName">
										<xsl:with-param name="id" select="@types"/>
									</xsl:call-template>
									<xsl:text>: "</xsl:text>
									<xsl:apply-templates select="topname/basename"/>
									<xsl:text>"</xsl:text>
								</fo:block>
							</fo:table-cell>
						</fo:table-row>
						<!-- render referenced topics, if present -->
						<xsl:if test="/topicmap/assoc[assocrl[1]=$ID]">
							<xsl:call-template name="render_spanned_columns">
								<xsl:with-param name="text" select="'referenzierte Topics'"/>
							</xsl:call-template>
							<xsl:call-template name="render_legend">
								<xsl:with-param name="left" select="'Assoziationstyp'"/>
								<xsl:with-param name="right" select="'Topic'"/>
							</xsl:call-template>
							<xsl:apply-templates select="/topicmap/assoc[assocrl[1]=$ID]">
								<xsl:sort select="@type"/>
								<xsl:with-param name="topic_pos" select="2"/>
							</xsl:apply-templates>
						</xsl:if>
						<!-- render referencing topics, if present -->
						<xsl:if test="/topicmap/assoc[assocrl[2]=$ID]">
							<xsl:call-template name="render_spanned_columns">
								<xsl:with-param name="text" select="'wird von folgenden Topics referenziert'"/>
							</xsl:call-template>
							<xsl:call-template name="render_legend">
								<xsl:with-param name="left" select="'Assoziationstyp'"/>
								<xsl:with-param name="right" select="'Topic'"/>
							</xsl:call-template>
							<xsl:apply-templates select="/topicmap/assoc[assocrl[2]=$ID]">
								<xsl:sort select="@type"/>
								<xsl:with-param name="topic_pos" select="1"/>
							</xsl:apply-templates>
						</xsl:if>
						<!-- render properties, if present -->
						<xsl:if test="property">
							<xsl:call-template name="render_spanned_columns">
								<xsl:with-param name="text" select="'Eigenschaften dieses Topics'"/>
							</xsl:call-template>
							<xsl:call-template name="render_legend">
								<xsl:with-param name="left" select="'Eigenschaft'"/>
								<xsl:with-param name="right" select="'Wert'"/>
							</xsl:call-template>
							<xsl:apply-templates select="property">
								<xsl:sort select="@name"/>
							</xsl:apply-templates>
						</xsl:if>
					</fo:table-body>
				</fo:table>
			</fo:block>
		</fo:block>
	</xsl:template>

	<!-- render multi-column headers -->
	<xsl:template name="render_spanned_columns">
		<xsl:param name="text" select="'test'"/>
		<fo:table-row>
			<fo:table-cell number-columns-spanned="3" padding-left="5pt" padding-right="5pt" background-color="{$color-refs-props}" border-width="2pt" border-color="white" border-style="solid">
				<fo:block font-style="italic" font-weight="bold"><xsl:value-of select="$text"/></fo:block>
			</fo:table-cell>
		</fo:table-row>
	</xsl:template>

	<!-- render names of columns -->
	<xsl:template name="render_legend">
		<xsl:param name="left" select="'test'"/>
		<xsl:param name="right" select="'test'"/>
		<fo:table-row>
			<fo:table-cell padding-left="5pt" padding-right="5pt" background-color="{$color-label-left}" border-width="2pt" border-color="white" border-style="solid">
				<fo:block font-style="italic" text-align="end">
					<xsl:value-of select="$left"/>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell/>
			<fo:table-cell padding-left="5pt" padding-right="5pt"  background-color="{$color-label-right}" border-width="2pt" border-color="white" border-style="solid">
				<fo:block font-style="italic">
					<xsl:value-of select="$right"/>
				</fo:block>
			</fo:table-cell>
		</fo:table-row>
	</xsl:template>

	<!-- render table rows -->
	<xsl:template name="render_table">
		<xsl:param name="left" select="'test'"/>
		<xsl:param name="right" select="'test'"/>
			<fo:table-row>
				<fo:table-cell padding-left="5pt" padding-right="5pt" background-color="{$color-field-left}" border-width="2pt" border-color="white" border-style="solid">
					<fo:block font-weight="bold" text-align="end">
						<xsl:copy-of select="$left"/>
					</fo:block>
				</fo:table-cell>
				<fo:table-cell/>
				<fo:table-cell padding-left="5pt" padding-right="5pt"  background-color="{$color-field-right}" border-width="2pt" border-color="white" border-style="solid">
					<fo:block>
						<xsl:copy-of select="$right"/>
					</fo:block>
				</fo:table-cell>
			</fo:table-row>
	</xsl:template>

	<!-- render topics in table rows -->
	<xsl:template match="topic" mode="table">
		<xsl:param name="topic_pos" select="'test'"/>
		<xsl:variable name="topicID2" select="assocrl[$topic_pos]"/>
		<xsl:call-template name="render_table">
			<xsl:with-param name="left">
				<xsl:call-template name="expandTopicTypeName">
					<xsl:with-param name="id" select="@types"/>
				</xsl:call-template>
			</xsl:with-param>
			<xsl:with-param name="right">
				<xsl:apply-templates select="." mode="link"/>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<!-- render associations as referenced/referencing topics in table rows -->
	<xsl:template match="assoc">
		<xsl:param name="topic_pos" select="'test'"/>
		<xsl:variable name="topicID2" select="assocrl[$topic_pos]"/>
		<xsl:call-template name="render_table">
			<xsl:with-param name="left">
				<xsl:call-template name="expandAssocTypeName">
					<xsl:with-param name="id" select="@type"/>
				</xsl:call-template>
			</xsl:with-param>
			<xsl:with-param name="right">
				<xsl:apply-templates select="/topicmap/topic[@ID=$topicID2]" mode="link"/>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<!-- render properties in table rows -->
	<xsl:template match="property">
		<xsl:param name="topic_pos" select="'test'"/>
		<xsl:variable name="topicID2" select="assocrl[$topic_pos]"/>
		<xsl:if test="*|text()">
			<xsl:call-template name="render_table">
				<xsl:with-param name="left">
				<xsl:apply-templates select="@name"/>
				</xsl:with-param>
				<xsl:with-param name="right">
					<xsl:apply-templates/>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<!-- render name of a topic referenced by 'id' parameter -->
	<!--
	<xsl:template name="expandTopicName">
		<xsl:param name="id" select="'test'"/>
		<xsl:apply-templates select="/topicmap/topic[@ID=$id][1]/topname/basename"/>
	</xsl:template>
	-->

	<!-- render name of a topictype referenced by 'id' parameter -->
	<xsl:template name="expandTopicTypeName">
		<xsl:param name="id" select="'test'"/>
		<xsl:apply-templates select="/topicmap/topictype[@ID=$id][1]/name"/>
	</xsl:template>

	<!-- render name of a assoctype referenced by 'id' parameter -->
	<xsl:template name="expandAssocTypeName">
		<xsl:param name="id" select="'test'"/>
		<xsl:apply-templates select="/topicmap/assoctype[@ID=$id][1]/name"/>
	</xsl:template>

	<!-- render topic icon -->
	<xsl:template name="expandTopicIcon">
		<xsl:param name="topic" select="'test'"/>
		<xsl:variable name="icon" select="$topic/@icon"/>
		<fo:external-graphic src="{$iconDirectory}{$icon}"/>
	</xsl:template>

	<!-- the following elements should not be rendered -->
	<xsl:template match="topictype"/>
	<xsl:template match="assoctype"/>
</xsl:stylesheet>
