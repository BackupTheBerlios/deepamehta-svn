<?xml version="1.0" encoding="UTF-8"?>

<!--
	date: 2003/10/19
	version: ks-1.1.19

	stylesheet for the transformation of XML-exported
	"Kompetenzstern"-Maps to formatting objects
-->
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:svg="http://www.w3.org/2000/svg"
	xmlns:xlink="http://www.w3.org/1999/xlink"
	xmlns:fo="http://www.w3.org/1999/XSL/Format"
	xmlns:fox="http://xml.apache.org/fop/extensions">

	<xsl:output method="xml" indent="yes"/>

	<!-- top-level parameters, submitted by the XSL processor -->

	<!-- directory which contains topic icons -->
	<xsl:param name="iconDirectory" select="'iconDirectory parameter not submitted by XSL processor'"/>

	<!-- top-level variables -->

	<xsl:variable name="reference_map" select="/topicmap[1]"/>
	<xsl:variable name="width" select="string($reference_map/Kompetenzstern/Geometry/BACKGROUND_WIDTH)"/>
	<xsl:variable name="height" select="string($reference_map/Kompetenzstern/Geometry/BACKGROUND_HEIGHT)"/>
	<xsl:variable name="kriterienType" select="string($reference_map/Kompetenzstern/KriterienType)"/>

	<!-- templates -->

	<xsl:template name="get_color">
		<xsl:param name="map_number" select="0"/>
		<xsl:choose>
			<xsl:when test="$map_number = 1">#FFFF00</xsl:when>
			<xsl:when test="$map_number = 2">#00FF00</xsl:when>
			<xsl:when test="$map_number = 3">#00FFFF</xsl:when>
			<xsl:when test="$map_number = 4">#0000FF</xsl:when>
			<xsl:when test="$map_number = 5">#FF00FF</xsl:when>
			<xsl:when test="$map_number = 6">#FF0000</xsl:when>
			<xsl:otherwise>#000000</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="/">
		<svg:svg width="{$width}px" height="{$height}px" font-size="10">
			<!-- render the coloured background -->
			<svg:rect x="0" y="0" width="{$width}" height="{$height}" fill="#F0F0E0"/>
			<!-- draw the "Kompetenzstern" background -->
			<xsl:apply-templates select="$reference_map/Kompetenzstern" mode="draw_background"/>
			<!-- iterate over all exported maps -->
			<xsl:for-each select="topicmap">
				<!-- determine the color to render the Kompetenzstern -->
				<xsl:variable name="color">
					<xsl:call-template name="get_color">
						<xsl:with-param name="map_number" select="position()"/>
					</xsl:call-template>
				</xsl:variable>
				<!-- render the specific competence stars -->
				<xsl:apply-templates mode="draw_assoc" select="assoc[@type='at-kompetenzstern']">
					<xsl:with-param name="topicmap" select="."/>
					<xsl:with-param name="fill_color" select="$color"/>
				</xsl:apply-templates>
				<!-- render agenda -->
				<svg:text x="{$width - 10}" y="{position() * 10 + 10}" fill="{$color}" text-anchor="end"><xsl:value-of select="@name"/></svg:text>
			</xsl:for-each>
			<!-- render the topics of the reference map, omitting the competence star elements -->
			<xsl:apply-templates select="$reference_map/topic[@types=$kriterienType and property[@name='Ordinal Number']]">
			<!--xsl:apply-templates select="$reference_map/*[not(@type='at-kompetenzstern' or @types='tt-bewertung')]"-->
				<xsl:with-param name="topicmap" select="$reference_map"/>
			</xsl:apply-templates>
		</svg:svg>
	</xsl:template>

	<!-- render Kompetenzstern specific elements -->
	<xsl:template match="Kompetenzstern" mode="draw_background">
		<xsl:variable name="x_center" select="Geometry/X_CENTER"/>
		<xsl:variable name="y_center" select="Geometry/Y_CENTER"/>
		<xsl:variable name="dist_center" select="Geometry/DIST_CENTER"/>
		<xsl:variable name="dist_circles" select="Geometry/DIST_CIRCLES"/>
		<!-- render assessment layers -->
		<xsl:for-each select="$reference_map/topic[@types='tt-bewertungsebene' and property[@name='Ordinal Number']]">
			<xsl:variable name="layer_count" select="last()"/>
			<xsl:variable name="total_height" select="$y_center * 2"/>
			<xsl:variable name="total_width" select="$x_center * 2"/>
			<xsl:variable name="y" select="($layer_count - position()) div $layer_count * $total_height"/>
			<xsl:variable name="height" select="$total_height div $layer_count"/>
			<svg:rect x="5" y="{$y + 5}" width="{$total_width - 10}" height="{$total_height div last() - 10}" fill="#D0D0D0"/>
			<svg:text x="10" y="{$y + 20}" font-family="Helvetica" font-weight="bold" fill="grey"><xsl:value-of select="topname/basename"/></svg:text>
		</xsl:for-each>
		<!-- render circles -->
		<svg:circle cx="{$x_center}" cy="{$y_center}" r="{$dist_center + (count(Werte/Wert) - 1) * $dist_circles}" fill="white" stroke="none"/>
		<xsl:for-each select="Werte/Wert">
			<xsl:variable name="r" select="$dist_center + (position() - 1) * $dist_circles"/>
			<svg:circle cx="{$x_center}" cy="{$y_center}" r="{$r}" fill="none" stroke="grey"/>
			<svg:text x="{$x_center}" y="{$y_center - ($dist_center + (position() - 1) * $dist_circles)}" fill="red"><xsl:apply-templates/></svg:text>
		</xsl:for-each>
		<!-- render radial rays -->
		<xsl:for-each select="$reference_map/topic[@types='tt-bewertung' and property[@name='Ordinal Number']]">
			<xsl:variable name="r0" select="$dist_center - $dist_circles"/>
			<xsl:variable name="r1" select="$dist_center + count($reference_map/Kompetenzstern/Werte/Wert) * $dist_circles"/>
			<xsl:variable name="rotate_angle" select="360 div last()"/>
			<xsl:variable name="starting_angle" select="$rotate_angle div 2 + 90"/>
			<svg:line stroke="#808080" x1="{$x_center + $r0}" y1="{$y_center}" x2="{$x_center + $r1}" y2="{$y_center}" transform="rotate({(position() - 1) * $rotate_angle + $starting_angle} {$x_center} {$y_center})"/>
		</xsl:for-each>
	</xsl:template>

	<!--
		Render associations.
		@param directed_assoc if true, a "directed association" will be drawn.
	 -->
	<xsl:template match="assoc" mode="draw_assoc">
		<xsl:param name="topicmap" select="$reference_map"/>
		<xsl:param name="directed_assoc" select="false"/>
		<xsl:param name="fill_color" select="@color"/>
		<xsl:variable name="topID1" select="assocrl[1]"/>
		<xsl:variable name="topID2" select="assocrl[2]"/>
		<xsl:variable name="x1" select="$topicmap/topic[@ID=$topID1]/@x"/>
		<xsl:variable name="y1" select="$topicmap/topic[@ID=$topID1]/@y"/>
		<xsl:variable name="x2" select="$topicmap/topic[@ID=$topID2]/@x"/>
		<xsl:variable name="y2" select="$topicmap/topic[@ID=$topID2]/@y"/>
		<xsl:choose>
			<xsl:when test="$directed_assoc">
				<svg:polygon fill="{$fill_color}" points="{$x1 - 2},{$y1} {$x1 + 2},{$y1} {$x2},{$y2}"/>
				<svg:polygon fill="{$fill_color}" points="{$x1},{$y1 - 2} {$x1},{$y1 + 2} {$x2},{$y2}"/>
			</xsl:when>
			<xsl:otherwise>
				<svg:line stroke="{$fill_color}" stroke-width="3" x1="{$x1}" y1="{$y1}" x2="{$x2}" y2="{$y2}"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- decide whether associations should be rendered "directed" or "not directed" -->
	<xsl:template match="assoc">
		<xsl:param name="topicmap" select="$reference_map"/>
		<xsl:variable name="assoctype" select="@type"/>
		<xsl:variable name="color" select="@color"/>
		<xsl:variable name="directed" select="not(@type='at-generic' or @type='at-kompetenzstern')"/>
		<!-- workaround in case of empty "color" attribute was dropped -->
		<xsl:apply-templates select="." mode="draw_assoc">
			<xsl:with-param name="topicmap" select="$topicmap"/>
			<xsl:with-param name="fill_color" select="$color"/>
			<xsl:with-param name="directed_assoc" select="$directed"/>
		</xsl:apply-templates>
	</xsl:template>

	<!--
		Decide whether to render a hyperlink for a topic
		(in case of a document topic) or to draw the topic only
	-->
	<xsl:template match="topic">
		<xsl:param name="topicmap" select="$reference_map"/>
		<xsl:choose>
			<xsl:when test="@types='tt-document'">
				<xsl:variable name="filename" select="property[@name='File']"/>
				<svg:a xlink:href="{$filename}">
					<xsl:apply-templates select="." mode="draw_topic">
						<xsl:with-param name="topicmap" select="$topicmap"/>
					</xsl:apply-templates>
				</svg:a>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select="." mode="draw_topic">
					<xsl:with-param name="topicmap" select="$topicmap"/>
				</xsl:apply-templates>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- render the topic -->
	<xsl:template match="topic" mode="draw_topic">
		<xsl:param name="topicmap" select="$reference_map"/>
		<xsl:variable name="x" select="@x"/>
		<xsl:variable name="y" select="@y"/>
		<xsl:variable name="topictype" select="@types"/>
		<xsl:variable name="color" select="$topicmap/topic[@types='tt-topictype' and @ID=$topictype]/property[@name='Color']"/>
		<xsl:variable name="icon" select="@icon"/>
		<svg:image x="{$x - 10}" y="{$y - 10}" width="20px" height="20px" xlink:href="{$iconDirectory}{$icon}"/>
		<xsl:if test="not(@types='tt-bewertung')">
			<svg:text x="{$x}" y="{$y + 20}" text-anchor="middle"    font-family="Helvetica" fill="black"><xsl:value-of select="topname/basename"/></svg:text>
		</xsl:if>
	</xsl:template>

	<!-- the following elements should not be rendered -->
	<xsl:template match="topictype"/>
	<xsl:template match="assoctype"/>
</xsl:stylesheet>
