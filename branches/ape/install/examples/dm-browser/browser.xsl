<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<!-- <xsl:output method="xml" indent="yes"/> -->

	<xsl:include href="../xml2html.xsl"/>

	<!-- Root Template -->

	<xsl:template match="/">
		<html>
			<head>
				<title>DeepaMehta Browser</title>
			</head>
			<body>
				<h2>DeepaMehta Browser</h2>
				<xsl:if test="page/@name!='Login'">
					Logged in as "<xsl:value-of select="page/param[@name='user']/topic/topname/basename"/>".<br/>
					<xsl:if test="page/@name!='Home'">
						Go to my <a href="controller?action=goHome">homepage.</a>
					</xsl:if>					
				</xsl:if>
				<xsl:apply-templates/>
			</body>
		</html>
	</xsl:template>

	<!-- Pages -->

	<xsl:template match="page[@name='Login']">
		<form>
			<table>
				<tr>
					<td width="60"><small>Username</small></td>
					<td><input type="text" name="Username"/></td>
				</tr>
				<tr>
					<td><small>Password</small></td>
					<td><input type="password" name="Password"/></td>
				</tr>
				<tr>
					<td></td>
					<td>
						<input type="submit" value="Login"/>
						<input type="hidden" name="action" value="tryLogin"/>
					</td>
				</tr>
			</table>
		</form>
	</xsl:template>

	<xsl:template match="page[@name='Home']">
		<h3>Your Workspaces</h3>
		<table>
			<xsl:apply-templates select="param[@name='workspaces']/topic" mode="topic-list"/>
		</table>
		<h3>Search</h3>
		<form>
			<input type="submit" value="Search Topic"/>
			<input type="text" name="search"/>
			<input type="hidden" name="action" value="search"/>
		</form>
	</xsl:template>

	<xsl:template match="page[@name='TopicForm']">
		<xsl:variable name="typeID" select="param[@name='typeID']"/>
		<xsl:variable name="topicParam" select="param[@name='topic']"/>
		<xsl:if test="not ($topicParam)">
			<h3>Create Topic</h3>
			<xsl:call-template name="form">
				<xsl:with-param name="typeID" select="$typeID"/>
				<xsl:with-param name="action" select="'createTopic'"/>
			</xsl:call-template>
		</xsl:if>
		<xsl:if test="$topicParam">
			<h3>Edit Topic</h3>
			<xsl:call-template name="form">
				<xsl:with-param name="typeID" select="$typeID"/>
				<xsl:with-param name="action" select="'updateTopic'"/>
				<xsl:with-param name="topic" select="$topicParam/topic"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template match="page[@name='TopicInfo']">
		<xsl:variable name="topic" select="param[@name='topic']/topic"/>
		<h3>
			<xsl:value-of select="$topic/topname/basename"/>&#160;
			<img src="{@baseURL}icons/{$topic/@icon}"/>&#160;
			(<small><xsl:value-of select="topictype[@ID=$topic/@types]/name"/></small>)
		</h3>
		<xsl:apply-templates select="$topic"/>
		<h3>Related Topics</h3>
		<table>
			<xsl:apply-templates select="param[@name='relTopics']/topic" mode="topic-list">
				<xsl:sort select="@types"/>
				<xsl:sort select="topname/basename"/>
			</xsl:apply-templates>
		</table>
	</xsl:template>

	<xsl:template match="page[@name='TopicList']">
		<xsl:variable name="mode" select="param[@name='mode']"/>
		<h3>
			<xsl:if test="$mode='byName'">
				<xsl:value-of select="count(param[@name='topics']/topic)"/> topics with
				"<xsl:value-of select="param[@name='search']"/>" inside name
			</xsl:if>
			<xsl:if test="$mode='byType'">
				<xsl:value-of select="count(param[@name='topics']/topic)"/> topics of type
				"<xsl:value-of select="param[@name='typeID']"/>"
			</xsl:if>
		</h3>
		<table>
			<xsl:apply-templates select="param[@name='topics']/topic" mode="topic-list">
				<xsl:sort select="@types"/>
				<xsl:sort select="topname/basename"/>
				<!-- <xsl:sort select="/page/topictype[@ID=@types]/name"/> -->
			</xsl:apply-templates>
		</table>
	</xsl:template>

	<!-- Lists -->

	<xsl:template match="topic" mode="topic-list">
		<xsl:variable name="typeID" select="@types"/>
		<tr valign="top">
			<td width="30">
				<img src="{/page/@baseURL}icons/{@icon}"/>
			</td>
			<td width="250">
				<a href="controller?action=showTopicInfo&amp;topicID={@ID}">
					<xsl:value-of select="topname/basename"/>
				</a>
			</td>
			<td width="150">
				<small><xsl:value-of select="/page/topictype[@ID=$typeID]/name"/></small>
			</td>
		</tr>
	</xsl:template>

</xsl:stylesheet>
