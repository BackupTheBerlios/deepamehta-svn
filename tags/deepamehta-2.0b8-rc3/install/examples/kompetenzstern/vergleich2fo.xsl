<?xml version="1.0" encoding="UTF-8"?>

<!--
	date: 2008/02/26
	version: ks-1.1.22

	stylesheet for the comparison of
	competence stars (KS) to formatting objects.

	Currently, there are the following limitations:

	* only Kriteria which are present in the reference KS
		are looked up in the other KS's - what is not present
		in the reference KS, will not be rendered

	* because the lookup of corresponding elements utilizes the
		name of the Kriteria, make sure that not two Kriteria
		with the same name are in the map
-->
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format"
	xmlns:fox="http://xml.apache.org/fop/extensions"
	xmlns:svg="http://www.w3.org/2000/svg"
	xmlns:xalan="http://xml.apache.org/xalan"
	exclude-result-prefixes="xalan"
>

	<xsl:output method="xml" indent="yes"/>



	<!-- stylesheet imports -->



	<!-- import HTML -> FO stylesheet -->
	<xsl:include href="stylesheets/html2fo.xsl"/>



	<!-- top-level variables -->



	<!-- the reference map ("master map") -->
	<xsl:variable name="reference_map" select="/topicmap[1]"/>

	<!-- association types -->
	<xsl:variable name="at-subcriterion" select="'at-composition'"/>
	<xsl:variable name="at-relateddocument" select="'at-relateddocument'"/>
	<!-- copy the kriterienType to a String to avoid multiple XPath resolution -->
	<xsl:variable name="kriterienType" select="string($reference_map/Kompetenzstern/KriterienType)"/>
	<!-- directory which contains topic icons -->
	<xsl:param name="iconDirectory" select="'iconDirectory parameter not submitted by XSL processor'"/>



	<!-- templates -->



	<!-- the main template -->
	<xsl:template match="/">
		<fo:root>
			<fo:layout-master-set>
				<fo:simple-page-master master-name="A4"
				page-height="29.7cm" page-width="21cm"
				margin-top="1cm" margin-bottom="1cm"
				margin-left="1.5cm" margin-right="1.5cm"
				widows="2" orphans="2">
					<fo:region-before extent="1cm"/>
					<fo:region-body margin-top="1cm" margin-bottom="2cm"/>
					<fo:region-after extent="2cm"/>
				</fo:simple-page-master>
			</fo:layout-master-set>
			<fo:page-sequence master-reference="A4" initial-page-number="1">
				<!-- header -->
				<fo:static-content flow-name="xsl-region-before">
					<fo:block font-style="italic">
						<fo:table>
							<fo:table-column column-width="4.5cm"/>
							<fo:table-column column-width="13.5cm"/>
							<fo:table-body>
								<fo:table-row>
									<fo:table-cell>
										<fo:block>
											Report:
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block text-align="end">
											<xsl:for-each select="topicmap">
												<xsl:value-of select="@name"/>
												<xsl:if test="not(position()=last())">
													<xsl:text>, </xsl:text>
												</xsl:if>
											</xsl:for-each>
										</fo:block>
									</fo:table-cell>
								</fo:table-row>
							</fo:table-body>
						</fo:table>
					</fo:block>
					<fo:block>
						<fo:leader leader-length="100%" leader-pattern="rule" rule-thickness="2pt" color="green"/>
					</fo:block>
				</fo:static-content>
				<!-- footer -->
				<fo:static-content flow-name="xsl-region-after">
					<fo:block>
						<fo:leader leader-length="100%" leader-pattern="rule" rule-thickness="2pt" color="green"/>
					</fo:block>
					<fo:block>
						<fo:table>
							<fo:table-column column-width="3cm"/>
							<fo:table-column column-width="12cm"/>
							<fo:table-column column-width="3cm"/>
							<fo:table-body>
								<fo:table-row>
									<fo:table-cell>
										<fo:block>
											<fo:external-graphic src="{$iconDirectory}../images/deepamehta-logo-tiny.png"/>
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-style="italic" text-align="center">
											Seite <fo:page-number/> von <fo:page-number-citation ref-id="last"/>
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block>
											<fo:external-graphic src="{$iconDirectory}../images/deepamehta-logo-tiny.png"/>
										</fo:block>
									</fo:table-cell>
								</fo:table-row>
							</fo:table-body>
						</fo:table>
					</fo:block>
				</fo:static-content>
				<!-- text flow with hyphenation (german) -->
				<fo:flow flow-name="xsl-region-body" hyphenate="true" language="de">
					<fo:block space-before="10pt" font-size="1.2em" font-weight="bold">
						Report:
						<xsl:for-each select="topicmap">
								<xsl:value-of select="@name"/>
								<xsl:if test="not(position()=last())">
									<xsl:text>, </xsl:text>
								</xsl:if>
						</xsl:for-each>
					</fo:block>
					<fo:block space-before="10pt">
						<fo:table>
							<fo:table-column column-width="6cm"/>
							<fo:table-column column-width="5cm"/>
							<fo:table-column column-width="4cm"/>
							<fo:table-column column-width="3cm"/>
							<fo:table-body>
									<fo:table-row font-weight="bold" font-size="1.2em">
										<fo:table-cell>
											<fo:block>Analyse</fo:block>
										</fo:table-cell>
										<fo:table-cell>
											<fo:block>Firma</fo:block>
										</fo:table-cell>
										<fo:table-cell>
											<fo:block>Erfasser</fo:block>
										</fo:table-cell>
										<fo:table-cell>
											<fo:block>Erfasst am</fo:block>
										</fo:table-cell>
									</fo:table-row>
								    <xsl:for-each select="topicmap">
										<!-- determine the color to render the Kompetenzstern -->
										<xsl:variable name="color">
											<xsl:call-template name="get_color">
												<xsl:with-param name="map_number" select="position()"/>
											</xsl:call-template>
										</xsl:variable>
										<fo:table-row background-color="{$color}">
											<fo:table-cell>
												<fo:block><xsl:apply-templates select="@name"/></fo:block>
											</fo:table-cell>
											<fo:table-cell>
												<fo:block><xsl:apply-templates select="property[@name='Firma']"/></fo:block>
											</fo:table-cell>
											<fo:table-cell>
												<fo:block><xsl:apply-templates select="property[@name='Erfasser']"/></fo:block>
											</fo:table-cell>
											<fo:table-cell>
												<fo:block><xsl:apply-templates select="property[@name='Datum']"/></fo:block>
											</fo:table-cell>
										</fo:table-row>
									</xsl:for-each>
							</fo:table-body>
						</fo:table>
					</fo:block>
					<!-- Kompetenzstern description -->
					<fo:block font-weight="bold" font-size="1.2em" space-before="10pt" space-after="5pt">
						Kurzbeschreibung
					</fo:block>
					<xsl:variable name="xpath">property[@name='Description']</xsl:variable>
					<xsl:call-template name="render_elements_from_maps">
						<xsl:with-param name="xpath" select="$xpath"/>
					</xsl:call-template>
					<!-- SVG figure -->
					<fo:block space-before="10pt">
						<fo:instream-foreign-object>
							<!-- marker for the SAX processor to insert the SVG graphics here -->
							<xsl:processing-instruction name="insert">svg</xsl:processing-instruction>
						</fo:instream-foreign-object>
					</fo:block>
					<!-- Kompetenzstern abstract -->
						<fo:block font-weight="bold" font-size="1.2em" space-before="10pt" space-after="5pt">
						Zusammenfassung
					</fo:block>
					<xsl:variable name="xpath">property[@name='Zusammenfassung']</xsl:variable>
					<xsl:call-template name="render_elements_from_maps">
						<xsl:with-param name="xpath" select="$xpath"/>
					</xsl:call-template>
					<!-- Kriterien -->
				    <xsl:apply-templates select="$reference_map/topic[@types='tt-bewertungsebene' and property[@name='Ordinal Number']]">
						<xsl:sort select="property[@name='Ordinal Number']"/>
					</xsl:apply-templates>
					<!-- TOC (table of contents) -->
					<fo:block break-before="page" font-weight="bold" font-size="1.2em" space-before="15pt" space-after="10pt">
						Inhaltsverzeichnis
					</fo:block>
					<fo:block>
						<fo:table>
							<fo:table-column column-width="15cm"/>
							<fo:table-column column-width="1.5cm"/>
							<fo:table-body>
								<xsl:apply-templates mode="toc" select="$reference_map/topic[@types='tt-bewertungsebene' and property[@name='Ordinal Number']]">
									<xsl:sort select="property[@name='Ordinal Number']"/>
								</xsl:apply-templates>
							</fo:table-body>
						</fo:table>
					</fo:block>
					<!-- marker for last page (= number of pages) -->
					<fo:block id="last"/>
				</fo:flow>
			</fo:page-sequence>
			<!-- FOP extension: PDF bookmarks -->
		    <xsl:apply-templates mode="bookmark" select="$reference_map/topic[@types='tt-bewertungsebene' and property[@name='Ordinal Number']]">
				<xsl:sort select="property[@name='Ordinal Number']"/>
			</xsl:apply-templates>
		</fo:root>
	</xsl:template>

	<!-- Utility template for the generation of KS-specific colors -->
	<xsl:template name="get_color">
		<xsl:param name="map_number" select="0"/>
		<xsl:choose>
			<xsl:when test="$map_number = 1">#FFFFDD</xsl:when>
			<xsl:when test="$map_number = 2">#DDFFDD</xsl:when>
			<xsl:when test="$map_number = 3">#DDFFFF</xsl:when>
			<xsl:when test="$map_number = 4">#DDDDFF</xsl:when>
			<xsl:when test="$map_number = 5">#FFDDFF</xsl:when>
			<xsl:when test="$map_number = 6">#FFDDDD</xsl:when>
			<xsl:otherwise>#000000</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!--
		Renders corresponding elements from different maps,
		each in its own color.
		@param xpath the path to the element starting from the topicmap
		@param topic_path (optional parameter, only for attached docs):
			Specifies the path to the Kriterium topic as origin of the
			search for assigned documents
	-->
	<xsl:template name="render_elements_from_maps">
		<xsl:param name="xpath" select="."/>
		<xsl:param name="topic_path" select="false()"/>
		<xsl:for-each select="/topicmap">
			<xsl:variable name="topicmap" select="."/>
			<xsl:variable name="topic" select="xalan:evaluate($topic_path)"/>
			<xsl:variable name="krit_id" select="$topic/@ID"/>
		    <xsl:variable name="assocs_to_documents" select="$topicmap/assoc[@type=$at-relateddocument and assocrl[1]=$krit_id]"/>
			<xsl:variable name="xpath_evaluated">
				<xsl:apply-templates select="xalan:evaluate($xpath)"/>
			</xsl:variable>
			<xsl:variable name="color">
				<xsl:call-template name="get_color">
					<xsl:with-param name="map_number" select="position()"/>
				</xsl:call-template>
			</xsl:variable>
			<xsl:if test="string-length($xpath_evaluated) > 31 or count($assocs_to_documents) > 0">
				<fo:block background-color="{$color}">
					<fo:block font-size="0.5em" font-weight="bold" text-align="end" color="grey">
						"<xsl:apply-templates select="@name"/>",
						<xsl:apply-templates select="property[@name='Erfasser']"/>,
						<xsl:apply-templates select="property[@name='Datum']"/>
					</fo:block>
					<xsl:copy-of select="$xpath_evaluated"/>
					<!-- assign documents -->
					<xsl:if test="count($assocs_to_documents) > 0">
						<fo:block font-style="italic" space-before="3pt" space-after="4pt">referenzierte Dokumente:</fo:block>
						<xsl:for-each select="$assocs_to_documents">
							<xsl:variable name="doc_id" select="assocrl[2]"/>
							<xsl:apply-templates select="$topicmap/topic[@ID=$doc_id]">
							</xsl:apply-templates>
						</xsl:for-each>
					</xsl:if>
				</fo:block>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>

	<!--
		Renders corresponding elements from different maps,
		each in its own color.
		@param xpath the path to the element starting from the topicmap
	-->
	<xsl:template name="render_bewertung">
		<xsl:param name="xpath" select="."/>
		<xsl:for-each select="/topicmap">
			<xsl:variable name="color">
				<xsl:call-template name="get_color">
					<xsl:with-param name="map_number" select="position()"/>
				</xsl:call-template>
			</xsl:variable>
			<fo:block background-color="{$color}">
				<fo:inline font-style="italic">
					Bewertung "<xsl:apply-templates select="@name"/>":
				</fo:inline>
				<fo:inline font-weight="bold">
					(<xsl:value-of select="xalan:evaluate($xpath)"/>)
				</fo:inline>
			</fo:block>
		</xsl:for-each>
	</xsl:template>

	<!--
		Following templates adapted from xml2fo_ks.xsl.
		Function: the structure of the reference KS is traversed
		as for a single KS, but for every Kriterium found the
		the corresponding Kriteria in the other maps are looked up
		and rendered. Thus, only the rendering routines
		are substituted by calls of the template
		"render_elements_from_maps". For limitations, see above.
	-->



	<!-- assessment layers -->
	<xsl:template match="topic[@types='tt-bewertungsebene' and property[@name='Ordinal Number']]">
		<xsl:variable name="eb_pos" select="property[@name='Ordinal Number']"/>
		<xsl:variable name="eb_number" select="$eb_pos + 1"/>
		<xsl:variable name="count" select="last()"/>
		<fo:block font-weight="bold" font-size="1.5em" space-before="20pt" space-after="5pt" id="{@ID}">
			<xsl:value-of select="$eb_number"/>. <xsl:apply-templates select="topname/basename"/>
		</fo:block>
		<xsl:variable name="xpath">topic[@types='tt-bewertungsebene' and property[@name='Ordinal Number'] and topname/basename='<xsl:value-of select="topname/basename"/>']/property[@name='Description']</xsl:variable>
		<xsl:call-template name="render_elements_from_maps">
			<xsl:with-param name="xpath" select="$xpath"/>
		</xsl:call-template>
		<xsl:apply-templates select="$reference_map/topic[@types=$kriterienType and property[@name='Ordinal Number']=$eb_pos]">
			<xsl:with-param name="section_number" select="concat($eb_number, '.1')"/>
			<xsl:with-param name="parent_topic_ids" select="''"/>
		</xsl:apply-templates>
		<xsl:apply-templates select="$reference_map/topic[@types=$kriterienType and property[@name='Ordinal Number']=$count*2-1-$eb_pos]">
			<xsl:with-param name="section_number" select="concat($eb_number, '.2')"/>
			<xsl:with-param name="parent_topic_ids" select="''"/>
		</xsl:apply-templates>
	</xsl:template>

	<!--
		Render criteria.
		
		Note that this template, in combination with the
		template "render_associated_criteria", forms a recursive loop.

		@param parent_topic_ids contains the topic IDs of
		all ancestor topics to avoid navigation short circuits
	-->
	<xsl:template match="topic[@types=$kriterienType]">
		<xsl:param name="section_number" select="test"/>
		<xsl:param name="parent_topic_ids" select="'test'"/>
		<xsl:if test="not(contains($parent_topic_ids, @ID))">
			<fo:block font-weight="bold" font-size="1.2em" space-before="15pt" space-after="3pt" id="{@ID}">
				<xsl:value-of select="$section_number"/>. <xsl:apply-templates select="topname/basename"/>
			</fo:block>
			<!-- render "Bewertung" -->
			<xsl:variable name="xpath_bewertung">topic[@types=$kriterienType and topname/basename='<xsl:value-of select="topname/basename"/>']/property[@name='Wert']</xsl:variable>
			<xsl:call-template name="render_bewertung">
				<xsl:with-param name="xpath" select="$xpath_bewertung"/>
			</xsl:call-template>
			<fo:block space-before="5pt"/>
			<!-- render "Description" -->
			<xsl:variable name="xpath">topic[@types=$kriterienType and topname/basename='<xsl:value-of select="topname/basename"/>']/property[@name='Description']</xsl:variable>
			<xsl:variable name="topic_path">topic[@types=$kriterienType and topname/basename='<xsl:value-of select="topname/basename"/>']</xsl:variable>
			<xsl:call-template name="render_elements_from_maps">
				<xsl:with-param name="xpath" select="$xpath"/>
				<xsl:with-param name="topic_path" select="$topic_path"/>
			</xsl:call-template>
			<!-- render via "composition" associated topics -->
			<xsl:call-template name="render_associated_criteria">
				<xsl:with-param name="section_number" select="$section_number"/>
				<xsl:with-param name="parent_topic" select="."/>
				<xsl:with-param name="parent_topic_ids" select="concat($parent_topic_ids, ' ', @ID)"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<!-- render associated criteria -->
	<xsl:template name="render_associated_criteria">
		<xsl:param name="section_number" select="'test'"/>
		<xsl:param name="parent_topic" select="'test'"/>
		<xsl:param name="parent_topic_ids" select="'test'"/>
		<xsl:variable name="id" select="$parent_topic/@ID"/>
		<!-- render associated subcriteria -->
		<xsl:for-each select="$reference_map/assoc[@type=$at-subcriterion and assocrl[1]=$id]">
			<xsl:variable name="assoc_topic_id" select="assocrl[2]"/>
			<xsl:apply-templates select="$reference_map/topic[@types=$kriterienType and @ID=$assoc_topic_id]">
				<xsl:with-param name="section_number" select="concat($section_number, '.', string(position()))"/>
				<xsl:with-param name="parent_topic_ids" select="$parent_topic_ids"/>
			</xsl:apply-templates>
		</xsl:for-each>
	</xsl:template>

	<!-- render related document topics -->
	<xsl:template match="topic[@types='tt-relateddocument']">
		<!--xsl:param name="color" select="'white'"/-->
		<fo:block>
			<xsl:variable name="icon" select="@icon"/>
			<fo:external-graphic src="{$iconDirectory}{$icon}"/>
			<xsl:text>   </xsl:text>
			<fo:inline>
				"<xsl:value-of select="topname/basename"/>":
			</fo:inline>
			<xsl:variable name="filename" select="property[@name='File']"/>
			<xsl:choose>
				<xsl:when test="string-length($filename) > 0">
					<!--fo:basic-link external-destination="{$filename}" color="blue"-->
						<xsl:value-of select="$filename"/>
					<!--/fo:basic-link-->
				</xsl:when>
				<xsl:otherwise>
					(keine Datei zugewiesen)
				</xsl:otherwise>
			</xsl:choose>
		</fo:block>
	</xsl:template>



	<!-- templates for PDF bookmarks -->



	<!-- assessment layers as bookmarks -->
	<xsl:template mode="bookmark" match="topic[@types='tt-bewertungsebene' and property[@name='Ordinal Number']]">
		<xsl:variable name="eb_pos" select="property[@name='Ordinal Number']"/>
		<xsl:variable name="eb_number" select="$eb_pos + 1"/>
		<xsl:variable name="count" select="last()"/>
		<fox:outline internal-destination="{@ID}">
			<fox:label>
				<xsl:value-of select="$eb_number"/>. <xsl:apply-templates select="topname/basename"/>
			</fox:label>
		</fox:outline>
		<xsl:apply-templates mode="bookmark" select="$reference_map/topic[@types=$kriterienType and property[@name='Ordinal Number']=$eb_pos]">
			<xsl:with-param name="section_number" select="concat($eb_number, '.1')"/>
			<xsl:with-param name="parent_topic_ids" select="''"/>
		</xsl:apply-templates>
		<xsl:apply-templates mode="bookmark" select="$reference_map/topic[@types=$kriterienType and property[@name='Ordinal Number']=$count*2-1-$eb_pos]">
			<xsl:with-param name="section_number" select="concat($eb_number, '.2')"/>
			<xsl:with-param name="parent_topic_ids" select="''"/>
		</xsl:apply-templates>
	</xsl:template>

	<!-- criteria as bookmarks -->
	<xsl:template mode="bookmark" match="topic[@types=$kriterienType]">
		<xsl:param name="section_number" select="test"/>
		<xsl:param name="parent_topic_ids" select="'test'"/>
		<xsl:if test="not(contains($parent_topic_ids, @ID))">
			<fox:outline internal-destination="{@ID}">
				<fox:label>
					<xsl:value-of select="$section_number"/>. <xsl:apply-templates select="topname/basename"/>
				</fox:label>
			</fox:outline>
			<!-- render via "composition" associated topics -->
			<xsl:call-template name="render_associated_criteria_bookmark">
				<xsl:with-param name="section_number" select="$section_number"/>
				<xsl:with-param name="parent_topic" select="."/>
				<xsl:with-param name="parent_topic_ids" select="concat($parent_topic_ids, ' ', @ID)"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<!-- render associated criteria for bookmarks -->
	<xsl:template name="render_associated_criteria_bookmark">
		<xsl:param name="section_number" select="'test'"/>
		<xsl:param name="parent_topic" select="'test'"/>
		<xsl:param name="parent_topic_ids" select="'test'"/>
		<xsl:variable name="id" select="$parent_topic/@ID"/>
		<!-- render associated subcriteria -->
		<xsl:for-each select="$reference_map/assoc[@type=$at-subcriterion and assocrl[1]=$id]">
			<xsl:variable name="assoc_topic_id" select="assocrl[2]"/>
			<xsl:apply-templates mode="bookmark" select="$reference_map/topic[@types=$kriterienType and @ID=$assoc_topic_id]">
				<xsl:with-param name="section_number" select="concat($section_number, '.', string(position()))"/>
				<xsl:with-param name="parent_topic_ids" select="$parent_topic_ids"/>
			</xsl:apply-templates>
		</xsl:for-each>
	</xsl:template>



	<!-- templates for TOC (table of contents) -->



	<!-- assessment layers as toc entries -->
	<xsl:template mode="toc" match="topic[@types='tt-bewertungsebene' and property[@name='Ordinal Number']]">
		<xsl:variable name="eb_pos" select="property[@name='Ordinal Number']"/>
		<xsl:variable name="eb_number" select="$eb_pos + 1"/>
		<xsl:variable name="count" select="last()"/>
		<fo:table-row space-before="10pt">
			<fo:table-cell>
				<fo:block>
					<fo:basic-link internal-destination="{@ID}">
						<xsl:value-of select="$eb_number"/>. <xsl:apply-templates select="topname/basename"/>
					</fo:basic-link>
					<fo:leader leader-pattern="dots" leader-pattern-width="8pt" leader-alignment="reference-area"/>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell>
				<fo:block text-align="end">
					<xsl:text>S. </xsl:text>
		          <fo:page-number-citation ref-id="{@ID}"/>
				</fo:block>
			</fo:table-cell>
		</fo:table-row>
		<xsl:apply-templates mode="toc" select="$reference_map/topic[@types=$kriterienType and property[@name='Ordinal Number']=$eb_pos]">
			<xsl:with-param name="section_number" select="concat($eb_number, '.1')"/>
			<xsl:with-param name="parent_topic_ids" select="''"/>
		</xsl:apply-templates>
		<xsl:apply-templates mode="toc" select="$reference_map/topic[@types=$kriterienType and property[@name='Ordinal Number']=$count*2-1-$eb_pos]">
			<xsl:with-param name="section_number" select="concat($eb_number, '.2')"/>
			<xsl:with-param name="parent_topic_ids" select="''"/>
		</xsl:apply-templates>
	</xsl:template>

	<!-- criteria as toc entries -->
	<xsl:template mode="toc" match="topic[@types=$kriterienType]">
		<xsl:param name="section_number" select="test"/>
		<xsl:param name="parent_topic_ids" select="'test'"/>
		<xsl:if test="not(contains($parent_topic_ids, @ID))">
			<fo:table-row space-before="5pt">
				<fo:table-cell>
					<fo:block>
						<fo:basic-link internal-destination="{@ID}">
							<xsl:value-of select="$section_number"/>. <xsl:apply-templates select="topname/basename"/>
						</fo:basic-link>
						<fo:leader leader-pattern="dots" leader-pattern-width="8pt" leader-alignment="reference-area"/>
					</fo:block>
				</fo:table-cell>
				<fo:table-cell>
					<fo:block text-align="end">
						<xsl:text>S. </xsl:text>
					  <fo:page-number-citation ref-id="{@ID}"/>
					</fo:block>
				</fo:table-cell>
			</fo:table-row>
			<!-- render via "composition" associated topics -->
			<xsl:call-template name="render_associated_criteria_toc">
				<xsl:with-param name="section_number" select="$section_number"/>
				<xsl:with-param name="parent_topic" select="."/>
				<xsl:with-param name="parent_topic_ids" select="concat($parent_topic_ids, ' ', @ID)"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<!-- render associated criteria for toc entries -->
	<xsl:template name="render_associated_criteria_toc">
		<xsl:param name="section_number" select="'test'"/>
		<xsl:param name="parent_topic" select="'test'"/>
		<xsl:param name="parent_topic_ids" select="'test'"/>
		<xsl:variable name="id" select="$parent_topic/@ID"/>
		<!-- render associated subcriteria -->
		<xsl:for-each select="$reference_map/assoc[@type=$at-subcriterion and assocrl[1]=$id]">
			<xsl:variable name="assoc_topic_id" select="assocrl[2]"/>
			<xsl:apply-templates mode="toc" select="$reference_map/topic[@types=$kriterienType and @ID=$assoc_topic_id]">
				<xsl:with-param name="section_number" select="concat($section_number, '.', string(position()))"/>
				<xsl:with-param name="parent_topic_ids" select="$parent_topic_ids"/>
			</xsl:apply-templates>
		</xsl:for-each>
	</xsl:template>

	<!-- the following elements should not be rendered -->
	<xsl:template match="topictype"/>
	<xsl:template match="assoctype"/>
</xsl:stylesheet>
