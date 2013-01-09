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

<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/WEB-INF/jsp/include.jsp" %>

<%@ page import="java.net.URL" %>
<%@ page import="edu.du.penrose.systems.fedoraApp.FedoraAppConstants" %>
<%@ page import="edu.du.penrose.systems.fedoraApp.batchIngest.bus.BatchIngestThreadManager" %>
<%@ page import="edu.du.penrose.systems.fedoraApp.web.gwt.batchIngest.client.BatchIngestStatus" %>

<%
	String batchSetName = request.getParameter( BatchIngestStatus.BATCH_SET_REQUEST_PARAM ); 

	if( batchSetName == null )
	{
		batchSetName = (String) request.getSession().getAttribute( FedoraAppConstants.BATCH_SET_NAME_ATTRIBUTE );
	}
	
	URL pidReportURL = BatchIngestThreadManager.getPidReportURL( batchSetName );
	

	boolean haveReport = false;
	try {
		Object temp = pidReportURL.getContent();
		if ( temp != null ){
			haveReport = true;
		}
	}
	catch ( Exception e )
	{
		haveReport = false; // yes this is redundent
	}
	
	if ( ! haveReport && batchSetName.contains( FedoraAppConstants.REMOTE_TASK_NAME_SUFFIX ) )
	{
	    // TBD quick kludge, at the end of a remote ingest the reports are in the taskTemp dir and then 
	    // get moved during the NEXT timer call to the logs directory.
	    String urlString = pidReportURL.toString().replace( "taskTemp", "logs" );
	    pidReportURL = new URL( urlString );
	}
%>

<% pageContext.setAttribute( "pidReportURL", pidReportURL ); %> <!--  make variables visable EL expression-->
	
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Batch Ingest Results</title>
</head>
<body>

<br>
<table align="center">
	<tr>
		<td>
			<a href="index.htm">Home</a>&nbsp;|&nbsp;
		</td>
<!--		<td>-->
<!--			<a href="batchIngest.htm">Start New Ingest</a>&nbsp;|&nbsp;-->
<!--		</td>-->
		<td>
			<a href="batchIngestReport.htm?<%=BatchIngestStatus.BATCH_SET_REQUEST_PARAM%>=<%=batchSetName%>" target="_blank">View Ingest Report</a>
		</td>
	</tr>
</table>

<% try { %>
<pre>			
	<c:import url="${pidReportURL}" />		
	<% }
	catch (Exception e) {
	%>
	 The PID report is empty, check the ingest report for errors.
	<% } %>
</pre>

			
		

</body>
</html>