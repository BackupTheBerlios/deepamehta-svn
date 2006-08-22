<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<!-- Topic Info -->

	<xsl:template match="topic">
		<table border="0">
			<xsl:call-template name="topicInfo">
				<xsl:with-param name="typeID" select="@types"/>
				<xsl:with-param name="props" select="property"/>
			</xsl:call-template>
		</table>
		<table border="0">
			<tr bgcolor="#e8e8e8">
				<td width="50" height="50">
					<a href="controller?action=showTopicForm&amp;typeID={@types}&amp;topicID={@ID}">
						<img src="images/edit.gif" border="0"/>
					</a>
				</td>
				<td width="50" height="50">
					<a href="controller?action=deleteTopic&amp;topicID={@ID}">
						<img src="images/trash.gif" border="0"/>
					</a>
				</td>
				<xsl:if test="@types='tt-topictype'">
					<td width="50">
						<a href="controller?action=showTopics&amp;typeID={@ID}">
							<img src="images/eye.gif" border="0"/>
						</a>
					</td>
					<td width="50">
						<a href="controller?action=showTopicForm&amp;typeID={@ID}">
							<img src="images/create.gif" border="0"/>
						</a>
					</td>
				</xsl:if>
			</tr>
		</table>
	</xsl:template>

	<xsl:template name="topicInfo">
		<xsl:param name="typeID" select="UNDEFINED"/>
		<xsl:param name="props" select="UNDEFINED"/>
		<xsl:variable name="type" select="/page/topictype[@ID=$typeID]"/>
		<!-- supertypes first -->
		<xsl:if test="$type/@supertypeID">
			<xsl:call-template name="topicInfo">
				<xsl:with-param name="typeID" select="$type/@supertypeID"/>
				<xsl:with-param name="props" select="$props"/>
			</xsl:call-template>
		</xsl:if>
		<!-- -->
		<xsl:for-each select="$type/propertydef[@visualization!='hidden']">
			<xsl:variable name="propName" select="@name"/>
			<tr valign="top">
				<td width="200"><small><xsl:value-of select="$propName"/></small></td>
				<td>
					<xsl:if test="@visualization!='Text Editor' and @visualization!='Password Field'">
						<xsl:value-of select="$props[@name=$propName]"/>
					</xsl:if>
					<xsl:if test="@visualization='Text Editor'">
						<xsl:apply-templates select="$props[@name=$propName]/html/body"/>
					</xsl:if>
					<xsl:if test="@visualization='Password Field'">
						<xsl:attribute name="bgcolor">
							<xsl:value-of select="'#CCCCCC'"/>
						</xsl:attribute>
					</xsl:if>
				</td>
			</tr>
		</xsl:for-each>
	</xsl:template>

	<!-- ### stupid! -->

	<xsl:template match="body">
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="p">
		<p><xsl:apply-templates/></p>
	</xsl:template>

	<xsl:template match="pre">
		<pre><xsl:apply-templates/></pre>
	</xsl:template>

	<xsl:template match="ul">
		<ul><xsl:apply-templates/></ul>
	</xsl:template>

	<xsl:template match="ol">
		<ol><xsl:apply-templates/></ol>
	</xsl:template>

	<xsl:template match="li">
		<li><xsl:apply-templates/></li>
	</xsl:template>

	<xsl:template match="table">
		<table><xsl:apply-templates/></table>
	</xsl:template>

	<xsl:template match="tr">
		<tr><xsl:apply-templates/></tr>
	</xsl:template>

	<xsl:template match="td">
		<td><xsl:apply-templates/></td>
	</xsl:template>

	<xsl:template match="img">
		<xsl:if test="starts-with(@src, 'http:') or starts-with(@src, 'file:')">
			<img src="{@src}"/>
		</xsl:if>
		<xsl:if test="starts-with(@src, 'http:')=false and starts-with(@src, 'file:')=false">
			<img src="{/page/@baseURL}{@src}"/>
		</xsl:if>
	</xsl:template>

	<xsl:template match="b">
		<b><xsl:apply-templates/></b>
	</xsl:template>

	<xsl:template match="u">
		<u><xsl:apply-templates/></u>
	</xsl:template>

	<xsl:template match="i">
		<i><xsl:apply-templates/></i>
	</xsl:template>

	<xsl:template match="br">
		<br/>
	</xsl:template>

	<!-- Form -->

	<!-- parameter "topic" is optional -->
	<xsl:template name="form">
		<xsl:param name="typeID" select="UNDEFINED"/>
		<xsl:param name="action" select="UNDEFINED"/>
		<xsl:param name="topic" select="UNDEFINED"/>
		<!-- topic: <xsl:value-of select="$topic"/> -->
		<form>
			<table border="0">
				<xsl:call-template name="formFields">
					<xsl:with-param name="typeID" select="$typeID"/>
					<xsl:with-param name="topic" select="$topic"/>
				</xsl:call-template>
				<tr valign="bottom">
					<td height="40"></td>
					<td>
						<input type="submit" value="OK"/>
						<input type="hidden" name="typeID" value="{$typeID}"/>
						<input type="hidden" name="action" value="{$action}"/>
						<input type="hidden" name="id" value="{$topic/@ID}"/>
					</td>
				</tr>
			</table>
		</form>
	</xsl:template>

	<xsl:template name="formFields">
		<xsl:param name="typeID" select="UNDEFINED"/>
		<xsl:param name="topic" select="UNDEFINED"/>
		<xsl:variable name="type" select="/page/topictype[@ID=$typeID]"/>
		<!-- supertypes first -->
		<xsl:if test="$type/@supertypeID">
			<xsl:call-template name="formFields">
				<xsl:with-param name="typeID" select="$type/@supertypeID"/>
				<xsl:with-param name="topic" select="$topic"/>
			</xsl:call-template>
		</xsl:if>
		<!-- -->
		<xsl:for-each select="$type/propertydef[@visualization!='hidden']">
			<xsl:variable name="propName" select="@name"/>
			<tr valign="top">
				<td width="200"><small><xsl:value-of select="$propName"/></small></td>
				<td>
					<xsl:if test="@visualization='Input Field'">
						<input type="text" name="{$propName}" size="50">
							<xsl:attribute name="value">
								<xsl:value-of select="$topic/property[@name=$propName]"/>
							</xsl:attribute>
						</input>
					</xsl:if>
					<xsl:if test="@visualization='Multiline Input Field' or @visualization='Text Editor'">
						<textarea name="{$propName}" rows="5" cols="50">
							<xsl:value-of select="$topic/property[@name=$propName]"/>
						</textarea>
					</xsl:if>
					<!-- JUST DUMMIES FOR NOW -->
					<xsl:if test="@visualization='Options Menu'">
						<input type="text" name="{$propName}" size="50">
							<xsl:attribute name="value">
								<xsl:value-of select="$topic/property[@name=$propName]"/>
							</xsl:attribute>
						</input>
					</xsl:if>
					<xsl:if test="@visualization='Option Buttons'">
						<input type="text" name="{$propName}" size="50">
							<xsl:attribute name="value">
								<xsl:value-of select="$topic/property[@name=$propName]"/>
							</xsl:attribute>
						</input>
					</xsl:if>
					<xsl:if test="@visualization='Switch'">
						<input type="text" name="{$propName}" size="50">
							<xsl:attribute name="value">
								<xsl:value-of select="$topic/property[@name=$propName]"/>
							</xsl:attribute>
						</input>
					</xsl:if>
					<xsl:if test="@visualization='Password Field'">
						<xsl:attribute name="bgcolor">
							<xsl:value-of select="'#CCCCCC'"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:if test="@visualization='Date Chooser'">
						<input type="text" name="{$propName}" size="50">
							<xsl:attribute name="value">
								<xsl:value-of select="$topic/property[@name=$propName]"/>
							</xsl:attribute>
						</input>
					</xsl:if>
					<xsl:if test="@visualization='Time Chooser'">
						<input type="text" name="{$propName}" size="50">
							<xsl:attribute name="value">
								<xsl:value-of select="$topic/property[@name=$propName]"/>
							</xsl:attribute>
						</input>
					</xsl:if>
					<xsl:if test="@visualization='File Chooser'">
						<input type="text" name="{$propName}" size="50">
							<xsl:attribute name="value">
								<xsl:value-of select="$topic/property[@name=$propName]"/>
							</xsl:attribute>
						</input>
					</xsl:if>
				</td>
			</tr>
		</xsl:for-each>
	</xsl:template>

	<!-- -->

	<xsl:template name="dayChooser">
		<xsl:param name="day" select="0"/>
	</xsl:template>

</xsl:stylesheet>
