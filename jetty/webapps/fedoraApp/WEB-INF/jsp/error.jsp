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

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ page isErrorPage="true" import="java.io.PrintWriter" %>

<%@taglib prefix="fedoraAppTags" tagdir="/WEB-INF/tags"%>

  <html><body>
				  <p><i><font color="#6699CC" size="6"><b>FedoraApp</b></font></i> <br>
  <h1 style="color: red">Error!</h1>
  <br>
  <h3 style="color: blue">Please note what you did and notify an administrator of the following error message:</h3>

  <%
  if ( exception != null ){
 %>
  <b> <%=exception%> </b>
 <% } %>
 
 	<% pageContext.setAttribute( "httpRequestParam", request ); %> <!--  make variables visable EL expression-->
	<fedoraAppTags:displayServerStatusErrors httpRequest="${ httpRequestParam }" />

<br>
<br>

</body></html>