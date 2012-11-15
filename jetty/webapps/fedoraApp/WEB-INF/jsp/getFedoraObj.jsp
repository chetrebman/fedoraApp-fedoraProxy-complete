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

<%@ page import="edu.du.penrose.systems.fedoraApp.FedoraAppConstants" %>
<%@ include file="/WEB-INF/jsp/include.jsp" %>

<%@ page import="edu.du.penrose.systems.fedoraApp.ProgramProperties" %>
<%@ page import="edu.du.penrose.systems.fedoraApp.FedoraAppConstants" %>

<%@taglib prefix="fedoraAppTags" tagdir="/WEB-INF/tags"%>

<%
	ProgramProperties ppTest = ProgramProperties.getInstance( FedoraAppConstants.getServletContextListener().getProgramPropertiesURL() );
%>
<html>

<body>
  
<p>

<form:form commandName="getFedoraObjCmd">

<p align="center" ><i><font color="#6699CC" size="6"><b><spring:message code="applicationTitle" text="resource NOT found" /></b></font></i> <br>

	<table align="center">
		<tr>
			<td>
				<a href="index.htm">Home</a>
			</td>
		</tr>
		<tr>
		<td>
			<table  border="1" bgcolor="#F9FBFD"  CELLPADDING="4" CELLSPACING="1" >
		    <tr>
		        <td align="center"  colspan="2"><b><font color="#660099"><spring:message code="getFedoraObj.formTitle" text="resource NOT found" /></font></b></td>
		    </tr>
		    <tr>
		    	<td>
		    		<b><font color="#FF0000">*</font><spring:message code="getFedoraObj.pidInputBox" text="resource NOT found" /> &nbsp; </b>           
		    	</td>
		        <td>
		        	<b><form:input path="objectPID" size="30" /><FONT COLOR="red"> 
		               <form:errors	path="objectPID" /></FONT></b>           
		       </td>
		       </tr>    
				<tr>
					<td align="center" colspan="2"><b><font color="#FF0000">*
							Means Required Field</font></b></td>
				</tr>
		       <tr >
		       	<td align="center"  colspan="2" >
		       		<INPUT TYPE="submit" VALUE="Submit">
		       	</td>
		       </tr>
		    </table>  
  		</td>
  		</tr>
  	</table>
</form:form>
</body>

</html>