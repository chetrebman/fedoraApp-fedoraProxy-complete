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

<%@ page import="edu.du.penrose.systems.fedoraApp.ProgramProperties" %>
<%@ page import="edu.du.penrose.systems.fedoraApp.FedoraAppConstants" %>

<%

// batchSetname parameter is used when called by the viewRunningIngest.jsp page
String batch_set = request.getParameter( FedoraAppConstants.BATCH_SET_NAME_REQUEST_PARAM );

if ( batch_set == null )
{
	batch_set = (String) request.getSession().getAttribute( FedoraAppConstants.BATCH_SET_NAME_ATTRIBUTE );
}
%>


<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<META HTTP-EQUIV="Pragma" CONTENT="no-cache">

<link type="text/css" rel="stylesheet" href="batchIngestStatus.css">

<meta name="gwt:module" content="edu.du.penrose.systems.fedoraApp.web.gwt.services.BatchIngestThreadManagerService"/>
<script language="javascript" src="gwt.js"></script>

<!--  Make this variable available to gtk java code. -->
<script language="javascript">
    var sessionAttributes = {
        batch_set: "<%=batch_set%>"
     };    
</script> 

<title>Fedora Batch Status</title>

		<!--                                           -->
		<!-- This script loads your compiled module.   -->
		<!-- If you add any GWT meta tags, they must   -->
		<!-- be added before this line.                -->
		<!--                                           -->
<script language='javascript' src='edu.du.penrose.systems.fedoraApp.web.gwt.batchIngest.BatchIngestStatus.nocache.js'></script>

</head>

<body>

		<!-- OPTIONAL: include this if you want history support -->
<iframe src="javascript:''" id="__gwt_historyFrame" style="width:0;height:0;border:0"></iframe>
  
<p align="center" >
    <i><font color="#6699CC" size="6"><b><spring:message code="applicationTitle" text="resource NOT found" /></b></font></i> 
</p>
	
<table align="center">
	<tr>
		<td>
			<a href="/<%=FedoraAppConstants.getServletContextListener().getWebApplicatonName()%>/index.htm">Home</a>&nbsp;|&nbsp; <!-- TBD -->
		</td>
		<td>
			<a href="/<%=FedoraAppConstants.getServletContextListener().getWebApplicatonName()%>/batchIngest.htm">Start New Ingest</a>&nbsp;|&nbsp;
		</td>
		<td>
			<a href="/<%=FedoraAppConstants.getServletContextListener().getWebApplicatonName()%>/viewRunningIngests.htm">View Running Ingests</a>
		</td>
	</tr>
</table>
	

<form:form commandName="batchIngestOptions">
	
	<table align="center">
				<tr>
					<td colspan="2"> &nbsp;
						<table border="0" width="100%">
							<tr>
								<td align="right" width="50%" >
									<b>Added: </b>
								</td>
								<td align="center" id="totalFilesAddedSuccess"><!--  this will be replaced by gwt --></td>
							</tr>
							<tr>
								<td align="right" width="50%" >
									<b> <font color="red">Failed: </font></b>
								</td>
								<td align="center" id="totalFilesAddedFailed"><!--  this will be replaced by gwt --></td>
							</tr>
							<tr>
								<td align="right" width="50%" >
									<b>Updated: </b>
								</td>
								<td align="center" id="totalFilesUpdatedSuccess"><!--  this will be replaced by gwt --></td>
							</tr>
							<tr>
								<td align="right" width="50%" >
									<b> <font color="red">Failed: </font></b>
								</td>
								<td align="center" id="totalFilesUpdatedFailed"><!--  this will be replaced by gwt --></td>
							</tr>
							<tr>
								<td align="right" width="50%" >
									<b> Collection: </b>
								</td>
								<td align="center" id="collection"><!--  this will be replaced by gwt --></td>
							</tr>
							<tr>
								<td align="right" width="50%" >
									<b> Content Model: </b>
								</td>
								<td align="center" id="contentModel"><!--  this will be replaced by gwt --></td>
							</tr>
						</table>
					</td>
				</tr>
		    
		    
	</table>  
<br><br>

<table align="center" class="statusTable" >
	<tr>
		<td align="center">
			<table>		
				<tr>
				    <td id="scriptRunningIndicator"><!-- this will be replaced by gwt --></td>
					<td align="center" >Institution:</td>
					<td id="institution" class="highlight"><!--  this will be replaced by gwt --></td>
					<td>Batch Set:</td>
					<td ><div class="highlight" id="batchSetId"><%=batch_set%></div></td>
				</tr>	
			</table>
		</td>
	</tr>
	<tr>
		<td colspan="4">
			<table align="center" border="2" width="100%" >
				<tr>
					<td > 	
						<table>
							<tr>
								<td>Status: &nbsp;</td>
								<td class="highlight" id="statusBar"><!-- this will be replaced by gwt --><td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>
<br>
<div id="statusArea2">
</div>
<br>
	  	   	
<table align="center" class="statusTable">
	<tr>
		<td>
			<div id="bottonPanel" ></div>
		</td>
	</tr>
</table>

</form:form>
		
<br><br>
<table width="80%" align="center" border="1">
	<tr>
		<td id="statusBox"><!-- this will be replaced by gwt --></td>
	</tr>
</table>
<br><br>

<% if ( ProgramProperties.getInstance( FedoraAppConstants.getServletContextListener().getProgramPropertiesURL() ).getProperty(FedoraAppConstants.BATCH_INGEST_DISABLE_GET_HANDLE_PROPERTY) != null ) { %>
	<table align="center">
			<tr>
				<td  align="center"  colspan="2" >
					<blink><b><font color="red">WARNING: Access to Handle Server is DISABLED.</font></b></blink>
				</td>
			</tr>
	</table>
<% } %>
<br>
<br>
<div id="pageVersion"><!-- this will be replaced by gwt --></div>

<!-- just reload the page in an iframe to keep the session from timing out. -->	
<iframe src="heartbeat.html" width=100 height=100 frameborder=0 scrolling=no>
		
</body>
</html>