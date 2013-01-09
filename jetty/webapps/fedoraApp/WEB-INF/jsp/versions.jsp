<%--
/*
 * Copyright 2012 University of Denver
 * Author Chet Rebman
 * 
 * This file is part of FedoraApp.
 * 
 * FedoraApp is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * FedoraApp is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with FedoraApp.  If not, see <http://www.gnu.org/licenses/>.
*/
--%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/WEB-INF/jsp/include.jsp" %>

<%@ page import="java.net.URL" %>
<%@ page import="edu.du.penrose.systems.fedoraApp.FedoraAppConstants" %>

<html>

<HEAD>
<TITLE>Versions</TITLE>
</HEAD>
<BODY> 

<h2>Fedora Web Client / Batch Ingest versions</h2>

<%
    URL versionsFileURL = FedoraAppConstants.getServletContextListener().getVersionsFileURL();
%>

<% pageContext.setAttribute( "versionsFileURL", versionsFileURL ); %> <!--  make variables visable EL expression-->

<table align="left">
	<tr>
		<td>
			<a href="index.htm">Home</a> &nbsp;|&nbsp;
		</td>
		<td>
			<a href="batchIngest.htm">Start New Ingest</a>
		</td>
	</tr>
</table>
<br><br>
<pre>
<c:import url="${versionsFileURL}" />
</pre>
</BODY>
</html>
