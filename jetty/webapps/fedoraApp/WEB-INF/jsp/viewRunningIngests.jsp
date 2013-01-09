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

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/WEB-INF/jsp/include.jsp" %>

<%@ page import="java.util.*" %>

<%@ page import="edu.du.penrose.systems.fedoraApp.ProgramProperties" %>
<%@ page import="edu.du.penrose.systems.fedoraApp.FedoraAppConstants" %>
<%@ page import="edu.du.penrose.systems.fedoraApp.batchIngest.data.BatchIngestOptions" %>
<%@ page import="edu.du.penrose.systems.fedoraApp.tasks.WorkerTimer" %>
<%@ page import="edu.du.penrose.systems.fedoraApp.batchIngest.bus.*" %>
<%@ page import="edu.du.penrose.systems.fedoraApp.batchIngest.bus.BatchIngestController" %>

<%
	Map<String, BatchIngestThreadHolder> allThreads = BatchThreadManager.getBatchIngestThreads();	
%>



<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<meta http-equiv="refresh" content="5" > 
<title>Fedora Batch Ingest</title>
</head>
<body>


       
<% // Display title and fedoraApp Version %>

<p align="center" >
    <i><font color="#6699CC" size="6"><b><spring:message code="applicationTitle" text="resource NOT found" /></b></font></i> 
    <br>
    <a href="versions.htm">
    	<i><font color="#6699CC" size="6"><b><spring:message code="version" text="version NOT found" /></b></font></i> 
    </a>
	<br><br> 
</p>
		

	<table align="center">
		<tr>
			<td>
				<a href="index.htm">Home</a>
			</td>
			<td align="center"> 
				<b><font color="#660099"> Ingest Tasks ( manual, _REMOTE and background _TASK )  </font></b> <em>5 Second Refresh</em>
			</td>
		</tr>
		<tr>
		<td colspan="2">
			<table width="800px" align="center" border="1" bgcolor="#F9FBFD"  CELLPADDING="8" CELLSPACING="1" >
		        <tr> 
		       		 <th>BatchSetName</th><th>Institution</th><th>BatchSet</th><th>Status</th>
		        </tr>
		        
		        <% for( String key : allThreads.keySet()  ) { 
		        
		        	BatchIngestController ingestThread =  allThreads.get( key ).getBatchIngestThread();
		        %>
		        <tr> 
		       		 <td><a href="edu.du.penrose.systems.fedoraApp.web.gwt.batchIngest.BatchIngestStatus/batchIngestStatus.jsp?<%=FedoraAppConstants.BATCH_SET_NAME_REQUEST_PARAM %>=<%=ingestThread.getBatchSetName() %>"><%=ingestThread.getBatchSetName() %></a></td>
		       		 <td><%=ingestThread.getInstitution() %></td>
		       		 <td><%=ingestThread.getBatchSet() %></td>
		       		 <td><%=allThreads.get( key ).getStatusString()%></td>
		        </tr>
		        <% } %>
		        
		    </table>
		 </td>
		 </tr>
	</table>
		 
</body>
</html>