<%--
/*
 * Copyright 2011 University of Denver
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

	String batchSetName = request.getParameter( edu.du.penrose.systems.fedoraApp.web.gwt.batchIngest.client.BatchIngestStatus.BATCH_SET_REQUEST_PARAM ); 

	if( batchSetName == null )
	{
		batchSetName = (String) request.getSession().getAttribute( FedoraAppConstants.BATCH_SET_NAME_ATTRIBUTE );
	}

	URL ingestReportURL = BatchIngestThreadManager.getIngestReportURL( batchSetName );

	boolean haveReport = false;
	try {
		Object temp = ingestReportURL.getContent();
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
	    String urlString = ingestReportURL.toString().replace( "taskTemp", "logs" );
	    ingestReportURL = new URL( urlString );
	}
%>

<% pageContext.setAttribute( "ingestReportURL", ingestReportURL ); %> <!--  make variables visible in EL expression-->
	
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
			<a href="batchIngestPidReport.htm?<%=BatchIngestStatus.BATCH_SET_REQUEST_PARAM%>=<%=batchSetName%>" target="_blank">View PID Report</a>
		</td>
	</tr>
</table>

<br><br>

<% try { %>
<pre>			
	<c:import url="${ingestReportURL}" />		
	<% }
	catch (Exception e) {
	%>
	 The Ingest report is empty.
	<% } %>
</pre>	

</body>
</html>