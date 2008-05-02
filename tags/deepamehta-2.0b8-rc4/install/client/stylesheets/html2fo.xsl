<?xml version="1.0" encoding="UTF-8"?>

<!--
	date: 2001/10/26
	version: dm-2.0a13-pre2

	stylesheet for the transformation 
	of HTML tags into formatting objects
-->
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format">
	
	<xsl:output method="xml" indent="yes"/>



	<!-- paragraph level formatting -->



	<!-- adaption of HTML headline -->
	<xsl:template match="h2|H2">
		<fo:block font-size="1.1em" font-weight="bold" space-before="3pt" space-after="3pt">
			<xsl:apply-templates/>
		</fo:block>
	</xsl:template>
	
	<!-- adaption of HTML headline -->
	<xsl:template match="h3|H3">
		<fo:block font-weight="bold" space-before="3pt" space-after="3pt">
			<xsl:apply-templates/>
		</fo:block>
	</xsl:template>
	
	<!-- adaption of HTML paragraphs -->
	<xsl:template match="p|P">
		<fo:block space-before="3pt" space-after="3pt">
			<xsl:apply-templates/>
		</fo:block>
	</xsl:template>

	<!-- adaption of HTML unordered list -->
	<xsl:template match="ul|UL">
		<fo:list-block provisional-distance-between-starts="0.3cm" provisional-label-separation="0.15cm">
			<xsl:apply-templates select="li"/>
		</fo:list-block>
	</xsl:template>
	
	<!-- adaption of HTML unordered list items -->
	<xsl:template match="ul/li|UL/LI">
		<fo:list-item>
			<fo:list-item-label end-indent="label-end()">
				<fo:block>
					<fo:inline font-size="10pt" font-family="Symbol">&#183;</fo:inline>
				</fo:block>
			</fo:list-item-label>	
			<fo:list-item-body start-indent="body-start()">
				<fo:block>
					<xsl:apply-templates/>
				</fo:block>
			</fo:list-item-body>
		</fo:list-item>
	</xsl:template>



	<!-- character level formatting -->


	
	<!-- adaption of HTML boldtype style -->
	<xsl:template match="b|B">
		<fo:inline font-weight="bold">
			<xsl:apply-templates/>
		</fo:inline>
	</xsl:template>
	
	<!-- adaption of HTML italic style -->
	<xsl:template match="i|I">
		<fo:inline font-style="italic">
			<xsl:apply-templates/>
		</fo:inline>
	</xsl:template>

	<!-- adaption of HTML underline style -->
	<xsl:template match="u|U">
		<fo:inline text-decoration="underline">
			<xsl:apply-templates/>
		</fo:inline>
	</xsl:template>

</xsl:stylesheet>
