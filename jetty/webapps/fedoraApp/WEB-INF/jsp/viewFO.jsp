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

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ page import="org.fcrepo.server.types.gen.ObjectFields" %>
<%@ page import="edu.du.penrose.systems.fedoraApp.web.bus.GetFedorObjFormController" %>
<%@ page import="edu.du.penrose.systems.fedora.client.Administrator" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>View Fedora Object.</title>
</head>

<%
	ObjectFields fedoraObject   = ( ObjectFields ) request.getAttribute( "fedora.server.types.gen.ObjectFields" );
	Administrator administrator = (Administrator) request.getSession().getAttribute( "edu.du.penrose.systems.fedora.client.Administrator" );
	String objectPID            = request.getParameter( "objectPID" );
	
	if ( fedoraObject == null && administrator != null && objectPID != null ) {
		try {
			fedoraObject = GetFedorObjFormController.getFedoraObj( null, administrator.getAPIA(), objectPID );
		}
		catch ( Exception e ) {
			// nop	
		}
	}
	
%>

<body>
<table   align="center">
  <tr>
    <td width="100%" align="center">
				  <p><i><font color="#6699CC" size="6"><b>View Fedora Object.</b></font></i> </p>
	</td>
  </tr>
  <tr>
    <td width="100%">
      <table border="0" width="100%">
        <tr>
          <td colspan="1"><A HREF="hello.htm">Home</A></td>
          <td colspan="1"><A HREF="searchFedora.htm">Search</A></td>
        </tr>
      </table>    
     </td>
  </tr>
  <tr>
    <td width="100%">
      <div align="center">
        <center>
        
			<% if ( fedoraObject != null ) {
		
				String state  =fedoraObject.getState();
				String label  =fedoraObject.getLabel();
				String cDate  =fedoraObject.getCDate();
				String mDate  =fedoraObject.getMDate();
				String ownerId=fedoraObject.getOwnerId();
			%>
				<b>
				<table border="1" align="center" > 
				<tr align="center" >
					<td colspan = "2" >Fedora Object</td>
				</tr>
				<tr>
					<td>PID =</td><td>   <%=objectPID%></td>
				</tr>
				<tr>
					<td>label =</td><td> <%=label%> </td>
				</tr>
				<tr>
					<td>cDate =</td><td> <%=cDate%> </td>
				</tr>
				<tr>
					<td>mDate =</td><td> <%=mDate%> </td>
				</tr>
				<tr>
					<td>ownerId =</td><td><%=ownerId%> </td>
				</tr>
				</table>
				</b>
		
			<% } else { %>
				Fedora object not found!
			<% } %>
	
		</center></div></td></tr></table>
		
</body>
</html>