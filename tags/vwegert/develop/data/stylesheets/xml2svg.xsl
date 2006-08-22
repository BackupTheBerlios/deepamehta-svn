<?xml version="1.0" encoding="UTF-8"?>

<!--
	date: 2003/10/19
	version: dm-2.0b2

	stylesheet for the transformation of
	XML-exported topicmaps to SVG graphic
-->
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:svg="http://www.w3.org/2000/svg"
	xmlns:xlink="http://www.w3.org/1999/xlink">

	<xsl:output method="xml" indent="yes"/>

	<!-- top-level parameters, submitted by the XSL processor -->

	<!-- directory which contains topic icons -->
	<xsl:param name="iconDirectory" select="'iconDirectory parameter not submitted by XSL processor'"/>

	<!-- templates -->

	<xsl:template match="/">
		<xsl:apply-templates/>
	</xsl:template>

	<!-- render figure frame -->
	<xsl:template match="topicmap">
		<xsl:variable name="width" select="500"/>
		<xsl:variable name="height" select="250"/>
		<svg:svg width="{$width}px" height="{$height}px">
			<!--svg:g transform="translate(0 {$width}) rotate(-90)"-->
			<svg:rect x="0" y="0" width="{$width}" height="{$height}" fill="#F0F0E0"/>
			<svg:g transform="scale(0.5) translate({$width div 2} {$height div 2})">
				<xsl:apply-templates select="assoc"/>
				<xsl:apply-templates select="topic"/>
			</svg:g>
		</svg:svg>
	</xsl:template>

	<!-- decide whether associations should be rendered "directed" or "not directed" -->
	<xsl:template match="assoc">
		<!-- <xsl:if test="true()"> -->
			<xsl:variable name="assoctype" select="@type"/>
			<xsl:variable name="color" select="@color"/>
			<xsl:variable name="directed" select="not(@type='at-generic' or @type='at-kompetenzstern')"/>
			<!-- workaround in case of empty "color" attribute was dropped -->
			<xsl:apply-templates select="." mode="draw_assoc">
				<xsl:with-param name="fill_color" select="$color"/>
				<xsl:with-param name="directed_assoc" select="$directed"/>
			</xsl:apply-templates>
		<!-- </xsl:if> -->
	</xsl:template>

	<!--
		Render associations.
		@param directed_assoc if true, a "directed association" will be drawn.
	 -->
	<xsl:template match="assoc" mode="draw_assoc">
		<xsl:param name="directed_assoc" select="false"/>
		<xsl:param name="fill_color" select="0"/>
		<xsl:variable name="topID1" select="assocrl[1]"/>
		<xsl:variable name="topID2" select="assocrl[2]"/>
		<xsl:variable name="x1" select="/topicmap/topic[@ID=$topID1]/@x"/>
		<xsl:variable name="y1" select="/topicmap/topic[@ID=$topID1]/@y"/>
		<xsl:variable name="x2" select="/topicmap/topic[@ID=$topID2]/@x"/>
		<xsl:variable name="y2" select="/topicmap/topic[@ID=$topID2]/@y"/>
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

	<!--
		Decide whether to render a hyperlink for a topic
		(in case of a document topic) or to draw the topic only
	-->
	<xsl:template match="topic">
		<!-- <xsl:if test="true()"> -->
			<xsl:choose>
				<xsl:when test="@types='tt-document'">
						<xsl:variable name="filename" select="property[@name='File']"/>
						<svg:a xlink:href="{$filename}">
							<xsl:apply-templates select="." mode="draw_topic"/>
						</svg:a>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates select="." mode="draw_topic"/>
				</xsl:otherwise>
			</xsl:choose>
		<!-- </xsl:if> -->
	</xsl:template>

	<!-- render the topic -->
	<xsl:template match="topic" mode="draw_topic">
		<xsl:variable name="x" select="@x"/>
		<xsl:variable name="y" select="@y"/>
		<xsl:variable name="topictype" select="@types"/>
		<xsl:variable name="color" select="/topicmap/topic[@types='tt-topictype' and @ID=$topictype]/property[@name='Color']"/>
		<xsl:variable name="icon" select="@icon"/>
		<svg:image x="{$x - 10}" y="{$y - 10}" width="20px" height="20px" xlink:href="{$iconDirectory}{$icon}"/>
		<xsl:if test="not(@types='tt-bewertung')">
			<svg:text x="{$x - 10}" y="{$y + 20}" font-family="Helvetica" fill="black"><xsl:value-of select="topname/basename"/></svg:text>
		</xsl:if>
	</xsl:template>

	<!-- the following elements should not be rendered -->
	<xsl:template match="topictype"/>
	<xsl:template match="assoctype"/>
</xsl:stylesheet>
