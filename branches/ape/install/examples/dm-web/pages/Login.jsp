<%@ include file="WebFrontend.jsp" %>

<html>
<head>
	<title>DeepaMehta Web Frontend</title>
</head>
<body>
	<h2>DeepaMehta Web Frontend</h2>
	<br>
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
<% end(session, out); %>
