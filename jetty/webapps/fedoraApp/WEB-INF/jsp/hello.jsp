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
<%@ include file="/WEB-INF/jsp/include.jsp" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Fedora test</title>
</head>
<body>

<div align="center">
        <center>
        
<p align="center" >
    <i><font color="#6699CC" size="6"><b><spring:message code="applicationTitle" text="resource NOT found" /></b></font></i> 
    <br>
	<br><br>
</p>
	<b>
	<a href="logout.htm">Logout</a><br><br>
<!-- 	<a href="searchFedora.htm">Search Fedora objects </a> -->
<!-- 	<br><br> -->
<!-- 	<a href="getFedoraObj.htm">Get Fedora object </a> -->
	<br>
	<br>
	<a href="batchIngest.htm">Fedora batch ingest </a>
	<br>
	<br>
	<br>
	<br>
	
	<a href="viewRunningIngests.htm">View running ingest tasks.</a>
	</b>
</center></div>

</body>
</html>