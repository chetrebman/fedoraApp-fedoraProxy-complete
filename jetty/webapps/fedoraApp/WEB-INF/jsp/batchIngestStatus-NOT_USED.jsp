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

<%@ page import="edu.du.penrose.systems.fedoraApp.ProgramProperties" %>
<%@ page import="edu.du.penrose.systems.fedoraApp.FedoraAppConstants" %>
<%@ page import="edu.du.penrose.systems.fedoraApp.batchIngest.bus.BatchIngestThreadManager" %>


<%
	boolean running = false;
	
	String batch_set = (String) request.getSession().getAttribute( FedoraAppConstants.BATCH_SET_NAME_ATTRIBUTE );
	
    if ( batch_set != null && BatchIngestThreadManager.isBatchSetRunning( batch_set ) ) 
    { 
  		running = true; 
    }
    else 
    {
  		request.getSession().removeAttribute( "DISABLE_STOP_INGEST_SUBMIT_BTN" ); // magic #
    }
    
    String test = (String) request.getSession().getAttribute( "DISABLE_STOP_INGEST_SUBMIT_BTN" ); // magic #
    boolean disableSubmitButton = false;
    if ( test != null ) {
        disableSubmitButton = true;
    }
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<META HTTP-EQUIV="Pragma" CONTENT="no-cache">
<meta http-equiv="refresh" content="5">

<meta name="gwt:module" content="edu.du.penrose.systems.fedoraApp.web.gwt.services.BatchIngestThreadManagerService"/>
<script language="javascript" src="gwt.js"></script>

<title>Fedora Batch Status</title>
</head>
<body>

  
<p align="center" >
    <i><font color="#6699CC" size="6"><b><spring:message code="applicationTitle" text="resource NOT found" /></b></font></i> 
    <br>
	<br><br>
</p>
	
<table align="center">
	<tr>
		<td>
			<a href="index.htm">Home</a>&nbsp;|&nbsp;
		</td>
		<td>
			<a href="batchIngest.htm">Start New Ingest</a>
		</td>
	</tr>
</table>
	
<form:form commandName="batchIngestOptions">
	
	<table align="center">
				<tr>
					<td colspan="2"> &nbsp;
						<table border="0" width="100%">
							<tr>
								<td colspan="2" align="center">
									<b><font color="#660099"><br><br><b>File Count</b></font></b>
								</td>
							</tr>
							<tr>
								<td align="right" width="50%" >
									<b>Ingested: </b>
								</td>
								<td align="center">&nbsp; 
									<%=BatchIngestThreadManager.getCurrentCompleted( batch_set )%>
								</td>
							</tr>
							<tr>
								<td align="right" width="50%" >
									<b> <font color="red">Failed: </font></b>
								</td>
								<td align="center">&nbsp;
									<%=BatchIngestThreadManager.getCurrentFailed( batch_set )%>
								</td>
							</tr>
						</table>
					</td>
				</tr>
		    
		    
	</table>  
<br><br>

<table align="center" >
	<tr>
		<td align="center">
			<b>&nbsp; Institution: &nbsp; <font color="#660099"><%=BatchIngestThreadManager.getInstitution( batch_set ) %></font> &nbsp; 
			    Batch Set: &nbsp; <font color="#660099"><%=batch_set %></font>&nbsp;</b>
		</td>
		</tr>
	<tr>
		<td>
			<table align="center" border="2" width="100%" >
				<tr>
					<td> 
						&nbsp;<b>Status: &nbsp; <%=BatchIngestThreadManager.getBatchSetStatus( batch_set ) %>&nbsp;</b>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>
		
<br><br>
<table align="center" >
	  	   	
	<% if ( ! running ) { %>
	    <tr>
	       	<td align="center"  colspan="2" >&nbsp;		       		
	       			<INPUT TYPE="submit" name="<%=FedoraAppConstants.BATCH_INGEST_VIEW_REPORT_BTN_NAME%>" VALUE="View Ingest Report" >		       		
	       	</td>
	    </tr>
	    
	    <tr>
	       	<td align="center"  colspan="2" >&nbsp;		       		
	       			<INPUT TYPE="submit" name="<%=FedoraAppConstants.BATCH_INGEST_VIEW_PID_REPORT_BTN_NAME%>" VALUE="View Pid Report" >		       		
	       	</td>
	    </tr>
	    
	    <tr>
	       	<td align="center"  colspan="2" ><br><br>       		
	       			<INPUT TYPE="submit" name="<%=FedoraAppConstants.BATCH_INGEST_ENABLE_NEW_BATCH_BTN_NAME%>" VALUE="Enable New Batch" >		       		
	       	</td>
	    </tr>
    <% } else { %>
    	<tr>
    		<td>
      		<% if (disableSubmitButton) { %>
      			<INPUT TYPE="submit" name="<%=FedoraAppConstants.BATCH_INGEST_STOP_INGEST_BTN_NAME%>" VALUE="Stop Ingest"  disabled="disabled" > 
      		<% } else { %>
      			<INPUT TYPE="submit" name="<%=FedoraAppConstants.BATCH_INGEST_STOP_INGEST_BTN_NAME%>" VALUE="Stop Ingest"> 
      	    <% } %>   	     	    
	   		</td>
	   </tr>
    <% } %>  
</table>


</form:form>

			
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

</body>
</html>