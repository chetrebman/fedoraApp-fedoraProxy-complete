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

<%@ page import="edu.du.penrose.systems.fedoraApp.web.data.SearchFedoraCmd" %>

<%@ page import="edu.du.penrose.systems.fedoraApp.ProgramProperties" %>
<%
  SearchFedoraCmd searchFC = ( SearchFedoraCmd ) request.getAttribute( "edu.du.penrose.systems.fedoraApp.web.data.SearchFedoraCmd" );
  String tableTitle = "";
  if ( searchFC != null ) {
	  tableTitle = "The search query was "+searchFC.getQuery();
  }
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Fedora Search Results</title>
</head>
<body>


<table   align="center">
  <tr>
    <td width="100%" align="center">
				  <p><i><font color="#6699CC" size="6"><b>Fedora Search Results</b></font></i> </p>
	</td>
  </tr>
  <tr>
    <td width="100%">
      <table border="0" width="100%">
        <tr>
          <td colspan="1"><A HREF="hello.htm">Home</A></td>
          <td colspan="1"><A HREF="getFedoraObj.htm">Get Fedora Object</A></td>
        </tr>
      </table>    
     </td>
  </tr>
  <tr>
    <td width="100%">
      <div align="center">
        <center>
        
			<% if (searchFC != null ) { %>
				<b>
				<table size="80%" align="center" border="1">
				<tr><th bgcolor="#c0c0c0" colspan="<%= searchFC.getResultFields().length %>"><%= tableTitle %></th></tr>
				<%
					out.println( "<tr bgcolor=\"#CCCC00\">" );
					Object[] resultFields = searchFC.getResultFields();
					for ( int field=0; field < resultFields.length; field++ ) {
						out.println( "<td align=\"center\">&nbsp;"+resultFields[ field ]+"&nbsp;</td>");
					}
					out.println( "</tr>" );
					
				    Object[][] fsData = searchFC.getFsDataResults();
					Object[] oneRow = null;
					for ( int row=0; row<fsData.length; row++ ) {
						out.println( "<tr>" );
						oneRow = fsData[ row ]; // ASSUME first field is PID TBD
						out.println( "<td align=\"center\">&nbsp;"+
								"<a href=\"viewFO.htm?objectPID="+oneRow[ 0 ]
								+"\">"+oneRow[ 0 ]+" </a> &nbsp;</td>");
						for ( int col=1; col< oneRow.length; col++ ) {
							out.println( "<td align=\"center\">&nbsp;"+oneRow[ col ]+"&nbsp;</td>");
						}
						out.println( "<tr>" );
					}
				%>
				</table>
				</b>		
			<% } %>

	</center>
  </div>
 </td>
 </tr>
</table>

</body>
</html>